package com.github.moribund.objects.playable.players;

import com.badlogic.gdx.InputProcessor;
import com.github.moribund.graphics.drawables.DrawableGameAsset;
import com.github.moribund.objects.attributes.Collidable;
import com.github.moribund.objects.attributes.Flaggable;
import com.github.moribund.objects.attributes.Movable;
import com.github.moribund.objects.playable.players.containers.ItemContainer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

/**
 * The {@code PlayableCharacter} interface is a template
 * for a character that can be interacted with by keys. All
 * playable characters are assumed as visible, movable, and
 * an input processor.
 */
public interface PlayableCharacter extends Collidable, Flaggable, DrawableGameAsset, Movable, InputProcessor {

    /**
     * Gets the player's unique ID generated by the connection.
     * @return The Player's unique ID.
     */
    int getPlayerId();

    int getGameId();

    /**
     * Binds the keys to the {@code keyBinds} using the
     * {@link com.badlogic.gdx.Input.Keys} constants to delegate
     * {@link Runnable} actions.
     */
    void bindKeys();

    /**
     * Gets the respective {@link PlayerAction} for the
     * {@link com.badlogic.gdx.Input.Keys} value pressed.
     * @return The key binds defined by {@link PlayableCharacter#bindKeys()}.
     */
    Int2ObjectMap<PlayerAction> getKeyBinds();

    /**
     * Handles the key being pressed by a player after it has gone through
     * client-server latency.
     * @param keyPressed The {@link com.badlogic.gdx.Input.Keys} value that was
     *                   pressed.
     */
    void keyPressed(int keyPressed);

    /**
     * Handles the key being lifted by a player after it has gone through
     * client-server latency.
     * @param keyUnpressed The {@link com.badlogic.gdx.Input.Keys} value that
     *                     was lifted.
     */
    void keyUnpressed(int keyUnpressed);

    ItemContainer getInventory();
}