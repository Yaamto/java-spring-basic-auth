package com.example.demo.service;

import com.example.demo.model.CarModel;
import com.example.demo.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarModel> getAllCars() {
        return carRepository.findAll();
    }

    public CarModel saveCar(CarModel car) {
        return carRepository.save(car);
    }
}
