package org.kate._4fileservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "files")
@Data // This generates getters/setters automatically
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String uploadDir;
    private String username;
}