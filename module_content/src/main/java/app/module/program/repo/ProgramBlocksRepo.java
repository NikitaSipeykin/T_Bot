package app.module.program.repo;

import app.module.program.dao.ProgramBlocks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramBlocksRepo extends JpaRepository<ProgramBlocks, String> {
  Optional<ProgramBlocks> findByName(String name);
  ProgramBlocks findByNextBlockIsNull();
}

