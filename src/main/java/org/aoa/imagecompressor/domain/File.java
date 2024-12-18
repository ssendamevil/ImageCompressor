package org.aoa.imagecompressor.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Setter
@Getter
public class File {
    private String fileName;
    private InputStream inputStream;
}
