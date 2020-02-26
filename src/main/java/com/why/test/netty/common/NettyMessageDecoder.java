package com.why.test.netty.common;

import com.why.test.netty.sbe.baseline.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NettyMessageDecoder extends ByteToMessageDecoder {
    private static final MessageHeaderDecoder MESSAGE_HEADER_DECODER = new MessageHeaderDecoder();
    private static final CarDecoder CAR_DECODER = new CarDecoder();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);
        byte[] bytes = new byte[byteBuf.readableBytes()];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);
        int bufferOffset = 0;
        MESSAGE_HEADER_DECODER.wrap(directBuffer, bufferOffset);
        // Lookup the applicable flyweight to decode this type of message based on templateId and version.
        final int templateId = MESSAGE_HEADER_DECODER.templateId();
        if (templateId != CarEncoder.TEMPLATE_ID) {
            throw new IllegalStateException("Template ids do not match");
        }

        final int actingBlockLength = MESSAGE_HEADER_DECODER.blockLength();
        final int actingVersion = MESSAGE_HEADER_DECODER.version();
        bufferOffset += MESSAGE_HEADER_DECODER.encodedLength();
        decode(CAR_DECODER, directBuffer, bufferOffset, actingBlockLength, actingVersion);
    }

    private static void decode(
            final CarDecoder car,
            final UnsafeBuffer directBuffer,
            final int bufferOffset,
            final int actingBlockLength,
            final int actingVersion)
            throws Exception {
        final byte[] buffer = new byte[128];
        final StringBuilder sb = new StringBuilder();

        car.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion);

        sb.append("\ncar.serialNumber=").append(car.serialNumber());
        sb.append("\ncar.modelYear=").append(car.modelYear());
        sb.append("\ncar.available=").append(car.available());
        sb.append("\ncar.code=").append(car.code());

        sb.append("\ncar.someNumbers=");
        for (int i = 0, size = CarEncoder.someNumbersLength(); i < size; i++) {
            sb.append(car.someNumbers(i)).append(", ");
        }

        sb.append("\ncar.vehicleCode=");
        for (int i = 0, size = CarEncoder.vehicleCodeLength(); i < size; i++) {
            sb.append((char) car.vehicleCode(i));
        }

        final OptionalExtrasDecoder extras = car.extras();
        sb.append("\ncar.extras.cruiseControl=").append(extras.cruiseControl());
        sb.append("\ncar.extras.sportsPack=").append(extras.sportsPack());
        sb.append("\ncar.extras.sunRoof=").append(extras.sunRoof());

        sb.append("\ncar.discountedModel=").append(car.discountedModel());

        final EngineDecoder engine = car.engine();
        sb.append("\ncar.engine.capacity=").append(engine.capacity());
        sb.append("\ncar.engine.numCylinders=").append(engine.numCylinders());
        sb.append("\ncar.engine.maxRpm=").append(engine.maxRpm());
        sb.append("\ncar.engine.manufacturerCode=");
        for (int i = 0, size = EngineEncoder.manufacturerCodeLength(); i < size; i++) {
            sb.append((char) engine.manufacturerCode(i));
        }
        sb.append("\ncar.engine.efficiency=").append(engine.efficiency());
        sb.append("\ncar.engine.boosterEnabled=").append(engine.boosterEnabled());
        sb.append("\ncar.engine.booster.boostType=").append(engine.booster().boostType());
        sb.append("\ncar.engine.booster.horsePower=").append(engine.booster().horsePower());

        sb.append("\ncar.engine.fuel=").append(
                new String(buffer, 0, engine.getFuel(buffer, 0, buffer.length), StandardCharsets.US_ASCII));

        for (final CarDecoder.FuelFiguresDecoder fuelFigures : car.fuelFigures()) {
            sb.append("\ncar.fuelFigures.speed=").append(fuelFigures.speed());
            sb.append("\ncar.fuelFigures.mpg=").append(fuelFigures.mpg());
            sb.append("\ncar.fuelFigures.usageDescription=").append(fuelFigures.usageDescription());
        }

        for (final CarDecoder.PerformanceFiguresDecoder performanceFigures : car.performanceFigures()) {
            sb.append("\ncar.performanceFigures.octaneRating=").append(performanceFigures.octaneRating());

            for (final CarDecoder.PerformanceFiguresDecoder.AccelerationDecoder acceleration : performanceFigures.acceleration()) {
                sb.append("\ncar.performanceFigures.acceleration.mph=").append(acceleration.mph());
                sb.append("\ncar.performanceFigures.acceleration.seconds=").append(acceleration.seconds());
            }
        }

        sb.append("\ncar.manufacturer=").append(car.manufacturer());

        sb.append("\ncar.model=").append(
                new String(buffer, 0, car.getModel(buffer, 0, buffer.length), CarEncoder.modelCharacterEncoding()));

        final UnsafeBuffer tempBuffer = new UnsafeBuffer(buffer);
        final int tempBufferLength = car.getActivationCode(tempBuffer, 0, tempBuffer.capacity());
        sb.append("\ncar.activationCode=").append(
                new String(buffer, 0, tempBufferLength, CarEncoder.activationCodeCharacterEncoding()));

        sb.append("\ncar.encodedLength=").append(car.encodedLength());

        System.out.println(sb);
    }
}
