package com.arnoldgalovics.inventory.service.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Product {
    private final UUID id;
    private final String name;
}
