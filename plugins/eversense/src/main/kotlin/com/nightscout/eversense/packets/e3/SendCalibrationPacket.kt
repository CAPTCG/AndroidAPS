package com.nightscout.eversense.packets.e3

import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.EversenseBasePacket
import com.nightscout.eversense.packets.EversensePacket
import com.nightscout.eversense.packets.e3.util.EversenseE3Writer

/**
 * Sends a blood glucose calibration value to the Eversense E3 transmitter.
 *
 * Packet structure matches the official Eversense app exactly
 * (decompiled from operationToSendBloodGlucoseValueToTransmitter):
 *
 * [0]    = 0x15 (21) — command ID, prepended by buildRequest()
 * [1-2]  = sampleDate — date of the BG measurement (2 bytes, FAT packed)
 * [3-4]  = sampleTime — time of the BG measurement (2 bytes, FAT packed)
 * [5-6]  = currentDate — date of submission = now (2 bytes, FAT packed)
 * [7]    = BG value LSB (direct int truncated to byte)
 * [8]    = BG value MSB
 * [9]    = BG value LSB (repeated — matches official app byte layout)
 * [10]   = 0x55 (rolling calibration enabled flag)
 * [11-12]= CRC16 LSB first, appended by buildRequest()
 *
 * @param glucoseMgDl  Blood glucose value in mg/dL
 * @param sampleTimeMs Timestamp of the BG measurement (defaults to now)
 */
@EversensePacket(
    requestId = EversenseE3Packets.SendBloodGlucoseDataCommandId,
    responseId = EversenseE3Packets.SendBloodGlucoseDataResponseId,
    typeId = 0,
    securityType = EversenseSecurityType.None
)
class SendCalibrationPacket(
    private val glucoseMgDl: Int,
    private val sampleTimeMs: Long = System.currentTimeMillis()
) : EversenseBasePacket() {

    override fun getRequestData(): ByteArray {
        val now = System.currentTimeMillis()

        val sampleDate = EversenseE3Writer.writeDate(sampleTimeMs)
        val sampleTime = EversenseE3Writer.writeTime(sampleTimeMs)
        val currentDate = EversenseE3Writer.writeDate(now)

        val bgLsb = (glucoseMgDl and 0xFF).toByte()
        val bgMsb = ((glucoseMgDl shr 8) and 0xFF).toByte()

        return byteArrayOf(
            sampleDate[0], sampleDate[1],   // [1-2] sample date
            sampleTime[0], sampleTime[1],   // [3-4] sample time
            currentDate[0], currentDate[1], // [5-6] current date (submission time)
            bgLsb,                          // [7]   BG LSB (direct)
            bgMsb,                          // [8]   BG MSB
            bgLsb,                          // [9]   BG LSB (repeated — matches official)
            0x55.toByte()                   // [10]  rolling calibration flag
        )
    }

    override fun parseResponse(): Response? {
        if (receivedData.isEmpty()) return null
        return Response()
    }

    class Response : EversenseBasePacket.Response()
}
