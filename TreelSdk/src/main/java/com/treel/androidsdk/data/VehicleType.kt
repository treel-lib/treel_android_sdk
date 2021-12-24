package com.treel.androidsdk.data

/**
 * @author Nitin Karande on 22/12/2021.
 */

enum class VehicleType(private val vehicleType: String) {
    CAR("Car"), SEDAN("Sedan"), HATCHBACK("Hatchback"), SUV("SUV"), BIKE("Bike"), TRAILER("Trailer"), TRUCK_5("Truck 5"), TRUCK_7("Truck 7"), TRUCK_10("Truck 10");

    override fun toString(): String = vehicleType
}