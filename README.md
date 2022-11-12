# RapidOcrAndroidOnnxCompose

[![Issue](https://img.shields.io/github/issues/RapidAI/RapidOcrAndroidOnnxCompose.svg)](https://github.com/RapidAI/RapidOcrAndroidOnnxCompose/issues)
[![Star](https://img.shields.io/github/stars/RapidAI/RapidOcrAndroidOnnxCompose.svg)](https://github.com/RapidAI/RapidOcrAndroidOnnxCompose)

<details open>
    <summary>目录</summary>

- [RapidOcrAndroidOnnx](#RapidOcrAndroidOnnx)
    - [联系方式](#联系方式)
    - [项目完整源码](#项目完整源码)
    - [APK下载](#APK下载)
    - [简介](#简介)
    - [总体说明](#总体说明)
    - [更新说明](#更新说明)
    - [编译说明](#编译说明)
        - [编译Release包](#编译Release包)
    - [常见问题](#常见问题)
        - [输入参数说明](#输入参数说明)
    - [关于作者](#关于作者)
    - [版权声明](#版权声明)

</details>

## 联系方式

* QQ①群：887298230 或 [连接](https://jq.qq.com/?_wv=1027&k=P9b3olx6)

## 项目完整源码

* 整合好源码和依赖库的完整工程项目，可到Q群共享内下载或Release下载，以Project开头的压缩包文件为源码工程，例：Project_RapidOcrAndroidOnnxCompose-版本号.7z
* 如果想自己折腾，则请继续阅读本说明

## APK下载

* 编译好的demo apk，可以在release中下载，或者Q群共享内下载，文件名例：RapidOcrAndroidOnnxCompose-版本号-release.apk

## 简介

RapidOcr onnxruntime推理 for Android

使用技术：jetpack compose + kotlin + 协程

## 与之前的版本不同点：

* RapidOcrAndroidOnnx的推理代码使用C++编写，再通过JNI调用
* RapidOcrAndroidOnnxCompose全部使用kotlin编写

## 主要使用的依赖库：

* onnxruntime[https://github.com/microsoft/onnxruntime](https://github.com/microsoft/onnxruntime)
* opencv[https://github.com/opencv/opencv](https://github.com/opencv/opencv)

## 更新说明

#### 2022-11-12 update 0.1.0

* 跑通完整识别流程
* opencv 4.6.0
* onnxruntime 1.13.1
* compose ui 1.3.1
* kotlin 1.7.10

## 编译说明

1. AndroidStudio 2021.3.1或以上；
2. 整合好的范例工程自带了模型，在OcrLibrary/src/main/assets文件夹中
3. 下载[opencv-4.6.0-android-sdk.zip](https://github.com/opencv/opencv/releases/tag/4.6.0)
   解压后目录结构为

```
项目根目录/sdk
    └── native
        ├── java
        ├── ……
        └── native
```

### 编译Release包

* mac/linux使用命令编译```./gradlew assembleRelease```
* win使用命令编译```gradlew.bat assembleRelease```
* 输出apk文件在app/build/outputs/apk

## 常见问题

* apk体积大？因为opencv没有经过裁剪，请自行折腾opencv裁剪编译。

### 输入参数说明

## 关于作者

* Android demo编写：[benjaminwan](https://github.com/benjaminwan)
* 模型来自：[PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR)

## 版权声明

- OCR模型版权归[PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR)所有；
- 其它工程代码版权归本仓库所有者所有；

