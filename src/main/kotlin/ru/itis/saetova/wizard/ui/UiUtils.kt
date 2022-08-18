package ru.itis.saetova.wizard.ui

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class ShiftKeyListener(val onShiftPressed: () -> Unit) : KeyListener {
    override fun keyTyped(e: KeyEvent) = Unit

    override fun keyPressed(e: KeyEvent) {
        if (e.keyCode == KeyEvent.VK_SHIFT) {
            onShiftPressed()
        }
    }

    override fun keyReleased(e: KeyEvent) = Unit
}