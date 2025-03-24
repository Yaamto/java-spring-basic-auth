package com.example.demo.controller;

import com.example.demo.model.CarModel;
import com.example.demo.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }
    @GetMapping("/admin/car")
    public ResponseEntity<List<CarModel>> index() {

        return ResponseEntity.ok(carService.getAllCars());
    }

    @PostMapping("/add")
    public CarModel addCar(@RequestBody CarModel car) {
        return carService.saveCar(car);
    }

}