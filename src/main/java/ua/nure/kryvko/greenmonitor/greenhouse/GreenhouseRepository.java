package ua.nure.kryvko.greenmonitor.greenhouse;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.kryvko.greenmonitor.user.User;

import java.util.List;

public interface GreenhouseRepository extends JpaRepository<Greenhouse, Integer> {
    List<Greenhouse> findByUser(User user);
}
