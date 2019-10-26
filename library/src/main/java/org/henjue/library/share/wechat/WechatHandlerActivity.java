/*
 * Copyright (C) 2015 Henjue, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.henjue.library.share.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.henjue.library.hnet.HNet;
import org.henjue.library.share.AuthListener;
import org.henjue.library.share.R;
import org.henjue.library.share.ShareListener;
import org.henjue.library.share.ShareSDK;
import org.henjue.library.share.api.WechatApiService;
import org.henjue.library.share.manager.WechatAuthManager;
import org.henjue.library.share.manager.WechatShareManager;


public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;

    private AuthListener mAuthListener;

    public static final String API_URL = "https://api.weixin.qq.com";

    /**
     * BaseResp的getType函数获得的返回值，1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;

    private Context mContext;
    private ShareListener mShareListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WechatHandlerActivity.this;
        mIWXAPI = WechatAuthManager.getIWXAPI();
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(final BaseResp resp) {


        mAuthListener = WechatAuthManager
                .getPlatformActionListener();
        mShareListener = WechatShareManager.getPlatformActionListener();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp.getType() == TYPE_LOGIN) {
                    final String code = ((SendAuth.Resp) resp).code;
                    WechatApiService api = getApiService();
                    api.getAccessToken(ShareSDK.getInstance().getWechatAppId(), ShareSDK.getInstance().getWechatSecret(), code, "authorization_code", new AccessTokenCallback(mAuthListener, api));
                } else {
                    mShareListener.onSuccess();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (resp.getType() == TYPE_LOGIN) {
                    if (mAuthListener != null) {
                        mAuthListener.onCancel();
                    }
                } else {
                    mShareListener.onCancel();
                }

                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                if (resp.getType() == TYPE_LOGIN) {
                    if (mAuthListener != null) {
                        mAuthListener.onError(new RuntimeException(resp.errStr));
                    }
                } else {
                    mShareListener.onFaild();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                if (resp.getType() == TYPE_LOGIN) {
                    if (mAuthListener != null) {
                        mAuthListener.onError(new RuntimeException(resp.errStr));
                    }
                    Toast.makeText(mContext, R.string.auth_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                break;
        }
        finish();
    }

    private WechatApiService getApiService() {
        return new HNet.Builder()
                .setEndpoint(API_URL)
                .build().create(WechatApiService.class);
    }
}
