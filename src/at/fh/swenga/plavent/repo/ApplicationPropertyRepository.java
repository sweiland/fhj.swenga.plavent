package at.fh.swenga.plavent.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import at.fh.swenga.plavent.model.ApplicationProperty;

@Repository
@Transactional
public interface ApplicationPropertyRepository  extends JpaRepository<ApplicationProperty, String> {
	
	public ApplicationProperty findFirstByToken(String token);

}
