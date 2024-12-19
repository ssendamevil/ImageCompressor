# ImageCompressor Benchmark

### Сжимаемое изображение: 
**Original image**: 2.7 MB  
![Original](https://github.com/user-attachments/assets/3cdf7a68-e178-407e-b063-fc630adfa19a)

#### PNG Compression
| Тип компрессии       | Используемая библиотека     | Уровень качества | Уменьшение размера | Время выполнения | Результат       |
|-----------------------|-----------------------------|------------------|---------------------|------------------|-----------------|
| **Lossy Compression** | Javax.ImageIO              | 30%             | 94%                 | 908 мс          | 2.7 MB → 152 kB |
|                       | Javax.ImageIO              | 50%             | 92%                 | 956 мс          | 2.7 MB → 210 kB |
|                       | Javax.ImageIO              | 90%             | 86%                 | 926 мс          | 2.7 MB → 379 kB |
| **Lossless Compression** | PngTastic                | 90%             | 7%                  | 16.14 сек       | 2.7 MB → 2.52 MB |

---

### Сжимаемое изображение:  
**Original image**: 175 kB  
![Original JPEG](https://github.com/user-attachments/assets/97c07096-edca-4af8-a968-594fa9131fcf)

#### JPEG (JPG) Compression
| Тип компрессии       | Используемая библиотека     | Уровень качества | Уменьшение размера | Время выполнения | Результат       |
|-----------------------|-----------------------------|------------------|---------------------|------------------|-----------------|
| **Lossy Compression** | Thumbnails                 | 30%             | 34%                 | 849 мс          | 175 kB → 115 kB |
|                       | Thumbnails                 | 50%             | 56%                 | 817 мс          | 175 kB → 77 kB  |
|                       | Thumbnails                 | 90%             | 81%                 | 692 мс          | 175 kB → 33 kB  |
|                       | Javax.ImageIO              | 30%             | 67%                 | 849 мс          | 175 kB → 58 kB  |
|                       | Javax.ImageIO              | 50%             | 56%                 | 817 мс          | 175 kB → 78 kB  |
|                       | Javax.ImageIO              | 70%             | 34%                 | 692 мс          | 175 kB → 115 kB |

---

### Сжимаемое изображение:
**Original image**: 2 MB  
![Original WEBP](https://github.com/user-attachments/assets/7796a4d5-29a6-4b4d-8cfc-9a90945fc3b1)

#### WEBP Compression
| Тип компрессии       | Используемая библиотека     | Уровень качества | Уменьшение размера | Время выполнения | Результат       |
|-----------------------|-----------------------------|------------------|---------------------|------------------|-----------------|
| **Lossy Compression** | Javax.Webp-ImageIO         | 30%             | 99%                 | 849 мс          | 2 MB → 25 kB    |
|                       | Javax.Webp-ImageIO         | 50%             | 98%                 | 1.85 сек        | 2 MB → 32 kB    |
|                       | Javax.Webp-ImageIO         | 90%             | 96%                 | 969 мс          | 2 MB → 89 kB    |

