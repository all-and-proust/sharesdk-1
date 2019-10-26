# Common Share SDK
## Build:
![build status](https://travis-ci.org/henjue/sharesdk.svg?branch=1.0.3_beta1)

## Last Version:
[ ![Download](https://api.bintray.com/packages/henjue/maven/sharesdk/images/download.svg) ](https://bintray.com/henjue/maven/sharesdk/_latestVersion)

## Last Version:
[ ![Download](https://api.bintray.com/packages/henjue/maven/sharesdk/images/download.svg) ](https://bintray.com/henjue/maven/sharesdk/_latestVersion)

Use Document to [See](http://www.j99.so/2015/06/24/Android-Share-Sdk-%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B/)!


## 常见问题
1、微薄没有返回结果
在你的发起登录请求的Activity的onActivityResult方法中加入如下代码：
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);  
    mSsoHandler = WeiboAuthManager.getSsoHandler();
    mShareHandler = WeiboShareManager.getShareHandler();
    if (mSsoHandler != null) {
        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        mShareHandler.doResultIntent(data,this);
    }
}
```
2、WeChat刚被调用就返回
* 可能为一开始的一些原因（如一开始签名填写错了）导致WeChat错误缓存导致，可以清空WeChat数据试试


## changelog:
1.0.2_beta3
* 修复一些错误

1.0.1(2015-06-24)
* 更新新浪weibo sdk到v3.1.1 支持64位手机

1.0.0(2015-06-23)
* 提供微信、新浪weibo、QQ统登录和共享功能
