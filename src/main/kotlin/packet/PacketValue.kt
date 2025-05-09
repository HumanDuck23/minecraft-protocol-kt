package dev.spaghett.packet

import io.netty.buffer.ByteBuf
import kotlin.reflect.KProperty

abstract class PacketValue<T : Any>(
    private val parent: Packet,
    protected var value: T,
    val readPredicate: (() -> Boolean)? = null
) {
    private fun finalInitialization() = parent.registerField(this)

    init {
        finalInitialization()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    abstract fun read(buffer: ByteBuf)
    abstract fun write(buffer: ByteBuf)
}