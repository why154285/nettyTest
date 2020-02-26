package com.why.test.netty.common;

import com.why.test.netty.sbe.baseline.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.sbe.MessageEncoderFlyweight;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NettyMessageEncoder extends MessageToByteEncoder<MessageEncoderFlyweight> {
    private static final byte[] VEHICLE_CODE;
    private static final byte[] MANUFACTURER_CODE;
    private static final byte[] MANUFACTURER;
    private static final byte[] MODEL;
    private static final UnsafeBuffer ACTIVATION_CODE;

    static {
        try {
            VEHICLE_CODE = "abcdef".getBytes(CarEncoder.vehicleCodeCharacterEncoding());
            MANUFACTURER_CODE = "123".getBytes(EngineEncoder.manufacturerCodeCharacterEncoding());
            MANUFACTURER = "Honda".getBytes(CarEncoder.manufacturerCharacterEncoding());
            MODEL = "Civic VTi".getBytes(CarEncoder.modelCharacterEncoding());
            ACTIVATION_CODE = new UnsafeBuffer("abcdef".getBytes(CarEncoder.activationCodeCharacterEncoding()));
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final CarEncoder CAR_ENCODER = new CarEncoder();
    private static final MessageHeaderEncoder MESSAGE_HEADER_ENCODER = new MessageHeaderEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageEncoderFlyweight messageEncoderFlyweight, ByteBuf byteBuf) throws Exception {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);

        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
        encode(CAR_ENCODER, directBuffer);
        int len = byteBuffer.limit() - byteBuffer.position();
        byte[] bytes1 = new byte[len];
        byteBuffer.get(bytes1);
        System.out.println(Arrays.toString(bytes1));
        System.out.println(bytes1.length);
        byteBuf.writeBytes(bytes1);
    }

    public static int encode(
            final CarEncoder car, UnsafeBuffer directBuffer) {
        car.wrapAndApplyHeader(directBuffer, 0, MESSAGE_HEADER_ENCODER)
                .serialNumber(1234)
                .modelYear(2013)
                .available(BooleanType.T)
                .code(Model.A)
                .putVehicleCode(VEHICLE_CODE, 0);

        car.putSomeNumbers(1, 2, 3, 4);

        car.extras()
                .clear()
                .cruiseControl(true)
                .sportsPack(true)
                .sunRoof(false);

        car.engine()
                .capacity(2000)
                .numCylinders((short) 4)
                .putManufacturerCode(MANUFACTURER_CODE, 0)
                .efficiency((byte) 35)
                .boosterEnabled(BooleanType.T)
                .booster().boostType(BoostType.NITROUS).horsePower((short) 200);

        car.fuelFiguresCount(3)
                .next().speed(30).mpg(35.9f).usageDescription("Urban Cycle")
                .next().speed(55).mpg(49.0f).usageDescription("Combined Cycle")
                .next().speed(75).mpg(40.0f).usageDescription("Highway Cycle");

        final CarEncoder.PerformanceFiguresEncoder figures = car.performanceFiguresCount(2);
        figures.next()
                .octaneRating((short) 95)
                .accelerationCount(3)
                .next().mph(30).seconds(4.0f)
                .next().mph(60).seconds(7.5f)
                .next().mph(100).seconds(12.2f);
        figures.next()
                .octaneRating((short) 99)
                .accelerationCount(3)
                .next().mph(30).seconds(3.8f)
                .next().mph(60).seconds(7.1f)
                .next().mph(100).seconds(11.8f);

        // An exception will be raised if the string length is larger than can be encoded in the varDataEncoding field
        // Please use a suitable schema type for varDataEncoding.length: uint8 <= 254, uint16 <= 65534
        car.manufacturer(new String(MANUFACTURER, StandardCharsets.UTF_8))
                .putModel(MODEL, 0, MODEL.length)
                .putActivationCode(ACTIVATION_CODE, 0, ACTIVATION_CODE.capacity());

        return MessageHeaderEncoder.ENCODED_LENGTH + car.encodedLength();
    }
}
