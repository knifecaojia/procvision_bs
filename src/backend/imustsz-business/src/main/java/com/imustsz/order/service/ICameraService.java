package com.imustsz.order.service;

import MvCameraControlWrapper.CameraControlException;

public interface ICameraService {

    byte[] getLatestPreviewImage();

    byte[] snapShotFromCache();

    int checkCamera() throws CameraControlException;
}
