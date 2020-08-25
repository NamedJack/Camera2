//
// Created by Administrator on 2020/8/25.
//

#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>

#include "native_color.h"

JNINativeMethod methods[] = {
        {"surfaceCreate",  "(I)V",  (void *) surfaceCreate},
        {"surfaceChanged", "(II)V", (void *) surfaceChanged},
        {"onDrawFrame",    "()V",   (void *) onDrawFrame}

};

jint registerNativeMethod(JNIEnv *env) {
    jclass clz = env->FindClass("com/android/camera2/gles/NativeColorRender");
    if ((env->RegisterNatives(clz, methods, sizeof(methods) / sizeof(methods[0]))) < 0) {
        return -1;
    } else
        return 0;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    if (registerNativeMethod(env) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL surfaceCreate(JNIEnv *env, jobject obj, jint color) {
    GLfloat redF = ((color >> 16) & 0xFF) / 1.0f / 255;
    GLfloat greenF = ((color >> 8) & 0xFF) / 1.0f / 255;
    GLfloat blueF = (color & 0xFF) / 1.0f / 255;
    GLfloat alphaF = ((color >> 32) & 0xFF) / 1.0f / 255;
    glClearColor(redF, greenF, blueF, alphaF);
}

JNIEXPORT void JNICALL surfaceChanged(JNIEnv *env, jobject obj, jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT void JNICALL onDrawFrame(JNIEnv *env, jobject obj) {
    glClear(GL_COLOR_BUFFER_BIT);
}
