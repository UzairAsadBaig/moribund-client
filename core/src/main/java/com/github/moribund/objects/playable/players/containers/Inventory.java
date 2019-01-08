package com.github.moribund.objects.playable.players.containers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.moribund.graphics.ui.DrawableUIAsset;

public class Inventory extends ItemContainer implements DrawableUIAsset {
    public Inventory() {
        super(3);
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).draw(i, spriteBatch);
        }
    }
}
