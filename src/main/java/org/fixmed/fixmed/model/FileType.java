package org.fixmed.fixmed.model;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;

@Getter
public enum FileType {
    JPG(new String[]{".jpg",".jpeg"}),
    PNG(new String[]{".png"});

    private final String[] extension;

    FileType(String[] extension) {
        this.extension = extension;
    }

    public boolean equalsExtension(String file) {
        return  Arrays.stream(extension)
                .anyMatch(s -> file.toLowerCase().endsWith(s));
    }

    @Nullable
    public static FileType getFileType(String file) {
        return Arrays.stream(values())
                .filter(value -> value.equalsExtension(file))
                .findFirst()
                .orElse(null);
    }


}
