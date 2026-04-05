package com.inventory.inventory_service;

import com.inventory.inventory_service.model.Inventory;
import com.inventory.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);

	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			saveIfMissing(inventoryRepository, "iphone_13", 100);
			saveIfMissing(inventoryRepository, "iphone_13_red", 5);
			saveIfMissing(inventoryRepository, "airpods_pro", 50);
			saveIfMissing(inventoryRepository, "macbook_pro", 20);
		};
	}

	private void saveIfMissing(InventoryRepository inventoryRepository, String skuCode, int quantity) {
		inventoryRepository.findBySkuCode(skuCode).ifPresentOrElse(
				inventory -> { },
				() -> {
					Inventory inventory = new Inventory();
					inventory.setSkuCode(skuCode);
					inventory.setQuantity(quantity);
					inventoryRepository.save(inventory);
				}
		);
	}

}
