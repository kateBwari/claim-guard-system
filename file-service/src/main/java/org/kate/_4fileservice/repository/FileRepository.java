package org.kate._4fileservice.repository;

import org.kate._4fileservice.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
}