package com.github.moribund.entity.flags.rotating;

import com.github.moribund.MoribundClient;
import com.github.moribund.entity.PlayableCharacter;
import com.github.moribund.entity.flags.Flag;
import com.github.moribund.net.packets.movement.RotationPacket;
import lombok.val;

abstract class RotateFlag implements Flag {
    void sendRotationPacket(PlayableCharacter playableCharacter) {
        val packetDispatcher = MoribundClient.getInstance().getPacketDispatcher();
        val rotationPacket = new RotationPacket(playableCharacter.getPlayerId(), playableCharacter.getRotation());
        packetDispatcher.sendUDP(rotationPacket);
    }
}
