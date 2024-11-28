import cv2

视频捕获对象 = cv2.VideoCapture('20240526_232652.mp4')
Haar级联分类器 = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

while True:
    是否成功读取到图像帧, 帧的图像数据 = 视频捕获对象.read()
    if not 是否成功读取到图像帧:
        break

    灰度图 = cv2.cvtColor(帧的图像数据, cv2.COLOR_BGR2GRAY)

    人脸坐标列表 = Haar级联分类器.detectMultiScale(
        灰度图,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30),
        flags=cv2.CASCADE_SCALE_IMAGE
    )

    for (x, y, w, h) in 人脸坐标列表:
        cv2.rectangle(帧的图像数据, (x, y), (x + w, y + h), (255, 0, 0), 2)

    cv2.imshow('Video', 帧的图像数据)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

视频捕获对象.release()
cv2.destroyAllWindows()