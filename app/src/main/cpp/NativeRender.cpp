////
//// Created by Administrator on 2020/8/25.
////
//
//#include <jni.h>
//#include <cstdio>
//#include <ctime>
//#include <android/bitmap.h>
//#include <android/native_window.h>
//#include <android/native_window_jni.h>
//#include <EGL/egl.h>
//#include <GLES3/gl3.h>
//
//#include "NativeRender.h"
//
//JNINativeMethod methods[] = {
//        {"drawColor",  "(Ljava/lang/Object;I)V",                  (void *) drawColor},
//        {"drawBitmap", "(Ljava/lang/Object;Ljava/lang/Object;)V", (void *) drawBitmap},
//};
//
//jint registerNativeMethod(JNIEnv *env) {
//    jclass cl = env->FindClass("com/android/camera2/gles/NativeRender");
//    if ((env->RegisterNatives(cl, methods, sizeof(methods) / sizeof(methods[0]))) < 0) {
//        return -1;
//    }
//    return 0;
//}
//
//jint JNI_OnLoad(JavaVM *vm, void *reserved) {
//    JNIEnv *env = NULL;
//    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
//        return -1;
//    }
//    if (registerNativeMethod(env) != JNI_OK) {
//        return -1;
//    }
//    return JNI_VERSION_1_6;
//}
//
//void ThrowException(JNIEnv *env, const char *exception, const char *message) {
//    jclass cl = env->FindClass(exception);
//    if (NULL != cl) {
//        env->ThrowNew(cl, message);
//    }
//}
//
//void drawColor(JNIEnv *env, jobject obj, jobject surface, jint colorARGB) {
//    int alpha = (colorARGB >> 24) & 0xFF;
//    int red = (colorARGB >> 16) & 0xFF;
//    int green = (colorARGB >> 8) & 0xFF;
//    int blue = colorARGB & 0xFF;
//
//    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
//    if (NULL == window) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to get native window");
//        return;
//    }
//
//    int32_t result = ANativeWindow_setBuffersGeometry(window, 640, 640, WINDOW_FORMAT_RGBA_8888);
//    if (result < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to set buffers geometry");
//        ANativeWindow_release(window);
//        window = NULL;
//        return;
//    }
//
//    ANativeWindow_acquire(window);
//    ANativeWindow_Buffer buffer;
//    if (ANativeWindow_lock(window, &buffer, NULL) < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to lock native window");
//        ANativeWindow_release(window);
//        window = NULL;
//        return;
//    }
//
//    uint32_t *line = (uint32_t *) buffer.bits;
//    for (int y = 0; y < buffer.height; y++) {
//        for (int x = 0; x < buffer.width; x++) {
//            line[x] = colorARGB;
//        }
//        line = line + buffer.stride;
//    }
//    if (ANativeWindow_unlockAndPost(window) < 0) {
//        ThrowException(env, "java/lang/RuntimeException",
//                       "unable to unlock and post to native window");
//    }
//    ANativeWindow_release(window);
//}
//
//void drawBitmap(JNIEnv *env, jobject obj, jobject surface, jobject bitmap) {
//    AndroidBitmapInfo info;
//    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to get bitmap info");
//        return;
//    }
//    char *data = NULL;
//    if (AndroidBitmap_lockPixels(env, bitmap, (void **) &data) < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to lock pixels");
//        return;
//    }
//    if (AndroidBitmap_unlockPixels(env, bitmap) < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to unlock pixels");
//        return;
//    }
//
//    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
//    if (NULL == window) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to get native window");
//        return;
//    }
//
//    int32_t result = ANativeWindow_setBuffersGeometry(window, info.width, info.height,
//                                                      WINDOW_FORMAT_RGBA_8888);
//
//    if (result < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to set buffers geometry");
//        ANativeWindow_release(window);
//        window = NULL;
//        return;
//    }
//    ANativeWindow_acquire(window);
//    ANativeWindow_Buffer buffer;
//
//    if (ANativeWindow_lock(window, &buffer, NULL) < 0) {
//        ThrowException(env, "java/lang/RuntimeException", "unable to lock native window");
//        //释放窗口
//        ANativeWindow_release(window);
//        window = NULL;
//        return;
//    }
//
//    int32_t *bitmapPixes = (int32_t *) data;
//
//    uint32_t *line = (uint32_t *) buffer.bits;
//    for (int y = 0; y < buffer.height; y++) {
//        for (int x = 0; x < buffer.width; x++) {
//            line[x] = bitmapPixes[buffer.height * y + x];
//        }
//        line = line + buffer.stride;
//    }
//
//    if (ANativeWindow_unlockAndPost(window) < 0) {
//        ThrowException(env, "java/lang/RuntimeException",
//                       "unable to unlock and post to native window");
//    }
//    ANativeWindow_release(window);
//
//
//}
