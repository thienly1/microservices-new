package ly.projects.inventoryservice.repository;

import ly.projects.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findAllBySkuCodeIn(List<String> skuCode);
}
