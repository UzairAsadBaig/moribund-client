package com.github.moribund.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.github.moribund.MoribundClient;
import com.github.moribund.images.SpriteContainer;
import com.github.moribund.images.SpriteFile;
import com.github.moribund.net.packets.KeyPressedPacket;
import com.github.moribund.net.packets.KeyUnpressedPacket;
import com.github.moribund.net.packets.LocationPacket;
import com.github.moribund.net.packets.RotationPacket;
import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

import java.util.HashSet;
import java.util.Set;

/**
 * The {@code Player} that is being controlled by a client. The {@code Player}
 * is a type of {@link InputProcessor} for it is bound to {@link Player#keyBinds}.
 */
public class Player extends PlayableCharacter {

    private static final int ROTATION_SPEED = 5;
    private static final int MOVEMENT_SPEED = 5;

    /**
     * The unique player ID based on the {@link com.esotericsoftware.kryonet.Connection} of
     * the client to the server.
     */
    @Getter
    private int playerId;
    /**
     * The {@link Sprite} of this {@code Player} that represents the {@code Player}
     * in the live game visually.
     */
    @Getter
    private Sprite sprite;
    /**
     * The respective {@link com.badlogic.gdx.Input.Keys} that are bound to
     * {@link Runnable} methods defined in this class.
     */
    private AbstractInt2ObjectMap<PlayerAction> keyBinds;
    /**
     * The currently active {@link Flag}s on the {@code Player}.
     */
    @Getter
    private Set<Flag> flags;

    @Getter
    private Set<Flag> flagsToRemove;

    /**
     * Makes a {@code Player} with its unique player ID generated by
     * the {@link com.esotericsoftware.kryonet.Connection} between the
     * client and the server.
     * @param playerId The unique player ID.
     */
    public Player(int playerId) {
        this.playerId = playerId;
        sprite = new Sprite(SpriteContainer.getInstance().getSprite(SpriteFile.DUMMY_PLAYER));
        flags = new HashSet<>();
        flagsToRemove = new HashSet<>();
    }

    /**
     * Flags a new {@link Flag} on the player.
     * @param flag The {@link Flag} to flag.
     */
    private void flag(Flag flag) {
        flags.add(flag);
    }

    /**
     * Removes a {@link Flag} on the player.
     * @param flag The {@link Flag} that is not longer active.
     */
    private void flagToRemove(Flag flag) {
        flagsToRemove.add(flag);
    }

    /**
     * Sends the current position of the {@code Player} to the
     * server so that positions can be updated server-sided.
     */
    private void sendLocationPacket() {
        val packetDispatcher = MoribundClient.getInstance().getPacketDispatcher();
        val tilePacket = new LocationPacket(playerId, getX(), getY());
        packetDispatcher.sendUDP(tilePacket);
    }

    private void sendRotationPacket() {
        val packetDispatcher = MoribundClient.getInstance().getPacketDispatcher();
        val rotationPacket = new RotationPacket(playerId, sprite.getRotation());
        packetDispatcher.sendUDP(rotationPacket);
    }

    @Override
    public void setRotation(float angle) {
        sprite.setRotation(angle);
    }

    @Override
    public void bindKeys() {
        keyBinds.put(Input.Keys.UP, new PlayerAction() {
            @Override
            public void keyPressed() {
                flag(Flag.MOVE_UP);
            }

            @Override
            public void keyUnpressed() {
                flagToRemove(Flag.MOVE_UP);
                sendLocationPacket();
            }
        });
        keyBinds.put(Input.Keys.DOWN, new PlayerAction() {
            @Override
            public void keyPressed() {
                flag(Flag.MOVE_DOWN);
            }

            @Override
            public void keyUnpressed() {
                flagToRemove(Flag.MOVE_DOWN);
                sendLocationPacket();
            }
        });
        keyBinds.put(Input.Keys.RIGHT, new PlayerAction() {
            @Override
            public void keyPressed() {
                flag(Flag.MOVE_RIGHT);
            }

            @Override
            public void keyUnpressed() {
                flagToRemove(Flag.MOVE_RIGHT);
                sendRotationPacket();
            }
        });
        keyBinds.put(Input.Keys.LEFT, new PlayerAction() {
            @Override
            public void keyPressed() {
                flag(Flag.MOVE_LEFT);
            }

            @Override
            public void keyUnpressed() {
                flagToRemove(Flag.MOVE_LEFT);
                sendRotationPacket();
            }
        });
    }

    @Override
    public float getX() {
        return sprite.getX();
    }

    @Override
    public float getY() {
        return sprite.getY();
    }

    @Override
    public void setX(float x) {
        sprite.setX(x);
    }

    @Override
    public void setY(float y) {
        sprite.setY(y);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    @Override
    public void rotateLeft() {
        sprite.rotate(ROTATION_SPEED);
    }

    @Override
    public void rotateRight() {
        sprite.rotate(-ROTATION_SPEED);
    }

    @Override
    public void moveForward() {
        val angle = sprite.getRotation();
        val xVelocity = MOVEMENT_SPEED * MathUtils.cosDeg(angle);
        val yVelocity = MOVEMENT_SPEED * MathUtils.sinDeg(angle);

        sprite.translate(xVelocity, yVelocity);
    }

    @Override
    public void moveBack() {
        val angle = sprite.getRotation();
        val xVelocity = -MOVEMENT_SPEED * MathUtils.cosDeg(angle);
        val yVelocity = -MOVEMENT_SPEED * MathUtils.sinDeg(angle);

        sprite.translate(xVelocity, yVelocity);
    }

    @Override
    public AbstractInt2ObjectMap<PlayerAction> getKeyBinds() {
        if (keyBinds == null) {
            keyBinds = new Int2ObjectOpenHashMap<>();
            bindKeys();
        }
        return keyBinds;
    }

    @Override
    public void keyPressed(int keyPressed) {
        getKeyBinds().get(keyPressed).keyPressed();
    }

    @Override
    public void keyUnpressed(int keyUnpressed) {
        getKeyBinds().get(keyUnpressed).keyUnpressed();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (getKeyBinds().containsKey(keycode)) {
            val player = MoribundClient.getInstance().getPlayer();
            val packetDispatcher = MoribundClient.getInstance().getPacketDispatcher();
            val keyPressedPacket = new KeyPressedPacket(player.getPlayerId(), keycode);
            packetDispatcher.sendUDP(keyPressedPacket);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (getKeyBinds().containsKey(keycode)) {
            val player = MoribundClient.getInstance().getPlayer();
            val packetDispatcher = MoribundClient.getInstance().getPacketDispatcher();
            val keyUnpressedPacket = new KeyUnpressedPacket(player.getPlayerId(), keycode);
            packetDispatcher.sendUDP(keyUnpressedPacket);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}