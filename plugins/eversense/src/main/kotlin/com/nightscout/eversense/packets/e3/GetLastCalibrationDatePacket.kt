package com.nightscout.eversense.packets.e3

import com.nightscout.eversense.enums.EversenseE3Memory
import com.nightscout.eversense.enums.EversenseSecurityType
import com.nightscout.eversense.packets.EversenseBasePacket
import com.nightscout.eversense.packets.EversensePacket

@EversensePacket(
    requestId = EversenseE3Packets.ReadSingleByteSerialFlashRegisterCommandId,
    responseId = EversenseE3Packets.ReadSingleByteSerialFlashRegisterResponseId,
    typeId = 0,
    securityType = EversenseSecurityType.None
)
class GetLastCalibrationDatePacket : EversenseBasePacket() {

    override fun getRequestData(): ByteArray {
        return EversenseE3Memory.LastCalibrationDate.getRequestData()
    }

    override fun parseResponse(): Response? {
        if (receivedData.isEmpty()) return null
        return Response(lowByte = receivedData[getStartIndex()].toInt() and 0xFF)
    }

    data class Response(val lowByte: Int) : EversenseBasePacket.Response()
}