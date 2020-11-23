package com.spy.spydemo;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SilentCamera2 implements Runnable{
    private Context context;
    private CameraDevice device;
    private ImageReader imageReader;
    private CameraCaptureSession session;
    private SurfaceTexture surfaceTexture;
    private CameraCharacteristics characteristics;
    private Surface previewSurface;
    private CaptureRequest.Builder request;
    private Handler handler;

    private String photosDir;

    public SilentCamera2(Context context) {
        this.context = context;
    }

    private final CameraDevice.StateCallback mStateCallback =
            new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    device = cameraDevice;

                    try {
                        surfaceTexture = new SurfaceTexture(10);
                        previewSurface = new Surface(surfaceTexture);

                        List<Surface> surfaceList = new ArrayList<>();
                        surfaceList.add(previewSurface);
                        surfaceList.add(imageReader.getSurface());

                        cameraDevice.createCaptureSession(surfaceList, mCaptureStateCallback, handler);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {

                }

                @Override
                public void onError(CameraDevice cameraDevice, int error) {

                }
            };

    private CameraCaptureSession.StateCallback mCaptureStateCallback =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession captureSession) {
                    session = captureSession;

                    try {
                        request = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        request.addTarget(previewSurface);

                        request.set(CaptureRequest.CONTROL_AF_TRIGGER,
                                CameraMetadata.CONTROL_AF_TRIGGER_START);

                        captureSession.setRepeatingRequest(request.build(), mCaptureCallback, handler);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession mCaptureSession) {}
            };

    private CameraCaptureSession.CaptureCallback mCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                }
            };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    Date date = new Date();

                    String filename = photosDir + "/" + dateFormat.format(date) + ".jpg";
                    File file = new File(filename);

                    Image image = imageReader.acquireLatestImage();

                    try {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);

                        OutputStream os = new FileOutputStream(file);
                        os.write(bytes);

                        image.close();
                        os.close();
                    } catch (Exception e) {
                        e.getStackTrace();
                    }

                    closeCamera();
                }
            };

    private void takePicture() {
        request.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());

        request.addTarget(imageReader.getSurface());
        try {
            session.capture(request.build(), mCaptureCallback, handler);
        } catch (CameraAccessException e) {
        }
    }

    private void closeCamera() {
        try {
            if (null != session) {
                session.abortCaptures();
                session.close();
                session = null;
            }
            if (null != device) {
                device.close();
                device = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
            if (null != surfaceTexture) {
                surfaceTexture.release();
            }
        } catch (Exception e) {
        }
    }

    public boolean takeSilentPhoto(String cam, String dir) {
        photosDir = dir;
        int facing;

        switch (cam) {
            case "front":
                facing = CameraCharacteristics.LENS_FACING_FRONT;
                break;
            case "back":
                facing = CameraCharacteristics.LENS_FACING_BACK;
                break;
            default:
                return false;
        }

        CameraManager manager = (CameraManager)
                context.getSystemService(Context.CAMERA_SERVICE);

        String cameraId = null;
        characteristics = null;

        try {
            for (String id : manager.getCameraIdList()) {
                characteristics = manager.getCameraCharacteristics(id);
                Integer currentFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (currentFacing != null && currentFacing == facing) {
                    cameraId = id;
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }

        HandlerThread handlerThread = new HandlerThread("CameraBackground");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        imageReader = ImageReader.newInstance(1920,1080, ImageFormat.JPEG, 2);
        imageReader.setOnImageAvailableListener(mOnImageAvailableListener, handler);

        try {
            manager.openCamera(cameraId, mStateCallback, handler);
            // Ждем фокусировку
            Thread.sleep(1000);
            takePicture();
        } catch (Exception e) {
            Log.d("TAG", "Can't open camera: " + e.toString());
            return false;
        }

        return true;
    }

    private int getOrientation() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = wm.getDefaultDisplay().getRotation();
        int deviceOrientation = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                deviceOrientation = 0;
                break;
            case Surface.ROTATION_90:
                deviceOrientation = 90;
                break;
            case Surface.ROTATION_180:
                deviceOrientation = 180;
                break;
            case Surface.ROTATION_270:
                deviceOrientation = 270;
                break;
        }

        int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;
        boolean facingFront = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    //этот код следует вызывать в отдельном потоке
    public void run(boolean front ){
        String cameraDir = context.getApplicationInfo().dataDir + "/camera/";
        if (front) {
            this.takeSilentPhoto("front", cameraDir);
        } else {
            this.takeSilentPhoto("back", cameraDir);
        }
    }

    @Override
    public void run() {

    }
}
