# Python OpenCV 图像处理指南

## 1. 图像增强

### 直方图均衡化
直方图均衡化是一种常用的图像增强技术，可以改善图像的对比度。

```python
import cv2
import matplotlib.pyplot as plt

def histogram_equalization(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)  # 以灰度模式读取图像
    
    # 应用直方图均衡化
    equalized_image = cv2.equalizeHist(image)
    
    # 显示原图和处理后的图像
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Equalized Image')
    plt.imshow(equalized_image, cmap='gray')
    plt.show()
```

## 2. 边缘检测

### Canny 边缘检测
Canny 是一种多级边缘检测算法，能够有效检测图像中的边缘。

```python
import cv2
import matplotlib.pyplot as plt

def canny_edge_detection(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 应用 Canny 边缘检测
    edges = cv2.Canny(image, 100, 200)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Edges')
    plt.imshow(edges, cmap='gray')
    plt.show()
```

### Hough 变换
用于检测图像中的直线和圆等几何形状。

```python
import cv2
import numpy as np
import matplotlib.pyplot as plt

def hough_line_detection(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 边缘检测
    edges = cv2.Canny(image, 50, 150)
    
    # Hough 变换检测直线
    lines = cv2.HoughLinesP(edges, 1, np.pi/180, threshold=100, 
                           minLineLength=100, maxLineGap=10)
    
    # 绘制检测到的直线
    line_image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
    for line in lines:
        x1, y1, x2, y2 = line[0]
        cv2.line(line_image, (x1, y1), (x2, y2), (0, 255, 0), 2)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Detected Lines')
    plt.imshow(line_image)
    plt.show()
```

## 3. 特征检测

### SIFT 特征检测
SIFT（Scale-Invariant Feature Transform）是一种用于图像特征检测和描述的算法。

```python
import cv2
import matplotlib.pyplot as plt

def sift_feature_detection(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 创建 SIFT 对象
    sift = cv2.SIFT_create()
    
    # 检测关键点和描述符
    keypoints, descriptors = sift.detectAndCompute(image, None)
    
    # 绘制关键点
    image_with_keypoints = cv2.drawKeypoints(image, keypoints, None, 
                                           flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Image with Keypoints')
    plt.imshow(image_with_keypoints)
    plt.show()
```

### SURF 特征检测
SURF（Speeded-Up Robust Features）是 SIFT 的一种快速版本。

```python
import cv2
import matplotlib.pyplot as plt

def surf_feature_detection(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 创建 SURF 对象
    surf = cv2.xfeatures2d.SURF_create(400)
    
    # 检测关键点和描述符
    keypoints, descriptors = surf.detectAndCompute(image, None)
    
    # 绘制关键点
    image_with_keypoints = cv2.drawKeypoints(image, keypoints, None, 
                                           flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Image with Keypoints')
    plt.imshow(image_with_keypoints)
    plt.show()
```

### ORB 特征检测
ORB（Oriented FAST and Rotated BRIEF）是一种高效的特征检测和描述算法。

```python
import cv2
import matplotlib.pyplot as plt

def orb_feature_detection(image_path):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 创建 ORB 对象
    orb = cv2.ORB_create()
    
    # 检测关键点和描述符
    keypoints, descriptors = orb.detectAndCompute(image, None)
    
    # 绘制关键点
    image_with_keypoints = cv2.drawKeypoints(image, keypoints, None, 
                                           flags=cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Image with Keypoints')
    plt.imshow(image_with_keypoints)
    plt.show()
```

## 4. 图像分割与降维

### K-Means 聚类
K-Means 是一种常用的聚类算法，可以用于图像分割。

```python
import cv2
import numpy as np
import matplotlib.pyplot as plt

def kmeans_segmentation(image_path, K=3):
    # 读取图像
    image = cv2.imread(image_path)
    
    # 将图像转换为二维数组
    Z = image.reshape((-1, 3))
    Z = np.float32(Z)
    
    # 定义 K-Means 参数
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10, 1.0)
    ret, label, center = cv2.kmeans(Z, K, None, criteria, 10, 
                                   cv2.KMEANS_RANDOM_CENTERS)
    
    # 重构图像
    center = np.uint8(center)
    res = center[label.flatten()]
    segmented_image = res.reshape((image.shape))
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    plt.subplot(1, 2, 2)
    plt.title('Segmented Image')
    plt.imshow(cv2.cvtColor(segmented_image, cv2.COLOR_BGR2RGB))
    plt.show()
```

### PCA 降维
主成分分析（PCA）是一种常用的数据降维技术。

```python
import cv2
import numpy as np
import matplotlib.pyplot as plt

def pca_compression(image_path, n_components=50):
    # 读取图像
    image = cv2.imread(image_path, 0)
    
    # 转换为浮点数组
    Z = image.reshape((-1, 1))
    Z = np.float32(Z)
    
    # PCA 处理
    mean, eigenvectors = cv2.PCACompute(Z, mean=None)
    projected = cv2.PCAProject(Z, mean, eigenvectors[:, :n_components])
    reconstructed = cv2.PCABackProject(projected, mean, 
                                     eigenvectors[:, :n_components])
    
    # 重构图像
    reconstructed_image = reconstructed.reshape(image.shape).astype(np.uint8)
    
    # 显示结果
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.title('Original Image')
    plt.imshow(image, cmap='gray')
    plt.subplot(1, 2, 2)
    plt.title('Reconstructed Image')
    plt.imshow(reconstructed_image, cmap='gray')
    plt.show()
```

## 5. 深度学习应用

### CNN 图像分类
使用卷积神经网络进行图像分类。

```python
import tensorflow as tf
from tensorflow.keras import layers, models

def create_cnn_model():
    model = models.Sequential([
        layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1)),
        layers.MaxPooling2D((2, 2)),
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.MaxPooling2D((2, 2)),
        layers.Conv2D(64, (3, 3), activation='relu'),
        layers.Flatten(),
        layers.Dense(64, activation='relu'),
        layers.Dense(10, activation='softmax')
    ])
    
    model.compile(optimizer='adam',
                 loss='sparse_categorical_crossentropy',
                 metrics=['accuracy'])
    return model

def train_mnist_classifier():
    # 加载数据
    (train_images, train_labels), (test_images, test_labels) = \
        tf.keras.datasets.mnist.load_data()
    
    # 数据预处理
    train_images = train_images.reshape((60000, 28, 28, 1)).astype('float32') / 255
    test_images = test_images.reshape((10000, 28, 28, 1)).astype('float32') / 255
    
    # 创建和训练模型
    model = create_cnn_model()
    history = model.fit(train_images, train_labels, epochs=5, 
                       batch_size=64, validation_split=0.2)
    
    return model, history
```

### YOLOv5 目标检测
使用 YOLOv5 进行实时目标检测。

```python
import torch
from PIL import Image

def yolo_detection(image_path):
    # 加载预训练模型
    model = torch.hub.load('ultralytics/yolov5', 'yolov5s')
    
    # 读取并处理图像
    image = Image.open(image_path)
    
    # 进行检测
    results = model(image)
    
    # 显示结果
    results.show()
```

## 使用注意事项

1. 在处理大型图像时注意内存使用
2. SIFT 和 SURF 在某些OpenCV版本中可能需要额外安装
3. 深度学习模型训练需要考虑GPU资源
4. 建议在使用前备份原始图像
5. 根据实际需求调整算法参数以获得最佳效果