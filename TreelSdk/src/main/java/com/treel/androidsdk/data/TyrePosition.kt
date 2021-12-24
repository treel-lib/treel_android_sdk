package com.treel.androidsdk.data

/**
 * @author Nitin Karande on 22/12/2021.
 */
enum class TyrePosition(private val position: String) {
    TYRE_1A("1A"),
    TYRE_1B("1B"),
    TYRE_2A("2A"),
    TYRE_2B("2B"),
    TYRE_2C("2C"),
    TYRE_2D("2D"),
    TYRE_3A("3A"),
    TYRE_3B("3B"),
    TYRE_3C("3C"),
    TYRE_3D("3D"),
    TYRE_4A("4A"),
    TYRE_4B("4B");

    override fun toString(): String = position
}
