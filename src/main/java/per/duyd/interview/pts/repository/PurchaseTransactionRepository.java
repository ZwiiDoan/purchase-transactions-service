package per.duyd.interview.pts.repository;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import per.duyd.interview.pts.entity.PurchaseTransaction;

public interface PurchaseTransactionRepository extends CrudRepository<PurchaseTransaction, UUID> {
}
