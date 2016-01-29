# Next Android 库总览

NextAndroid 是一个集成Android App开发工具库

## Events - 事件总线

类似Google Guava的EventBus，NextEvents提供一个事件总线处理库。它提供了线程回调，MainUI回调等非常有用的事件响应处理方式。

在 `AMD A8-5600K` / `8G` / `Ubuntu 14.04 LTS` / `Robolectric` 环境下测试结果：

Schedule类型及负载|发送速率|发送用时|执行负载总用时|调用负载总次数
----|----|----|----|----
MultiThreads(1ms Payload)|	 4374/s|		23ms|		457ms|		2000
SharedThread(1ms Payload)|	 4540/s|		4ms|		440ms|		2000
CallerThread(1ms Payload)|	 916/s|		2183ms|		2183ms|		2000
MultiThreads(Nop Payload)|	 1255396/s|		1593ms|		1593ms|		2000000
SharedThread(Nop Payload)|	 1300754/s|		1535ms|		1537ms|		2000000
CallerThread(Nop Payload)|	 5345735/s|		374ms|		374ms|		2000000


与SQUAREUP.OTTO对比情况如下表


Schedule类型及负载|发送速率|发送用时|执行负载总用时|调用负载总次数
----|----|----|----|----
MultiThreads(1ms Payload)|	 4385/s|		33ms|		456ms|		2000
SharedThread(1ms Payload)|	 4505/s|		8ms|		443ms|		2000
CallerThread(1ms Payload)|	 889/s|		2249ms|		2249ms|		2000
SQUARE.OTTO (1ms Payload)|	 910/s|		2195ms|		2195ms|		2000
MultiThreads(Nop Payload)|	 1354120/s|		1476ms|		1476ms|		2000000
SharedThread(Nop Payload)|	 1361032/s|		1469ms|		1469ms|		2000000
CallerThread(Nop Payload)|	 4239317/s|		471ms|		471ms|		2000000
SQUARE.OTTO (Nop Payload)|	 2361166/s|		846ms|		847ms|		2000000

## Click Proxy - 点击代理

基于NextEvents的扩展，一个@ClickEvt注解即快速实现绑定UI组件的点击处理处理。

## Inject View - 自动注入View

View自动注入处理，不再findViewById。

## Flux

单向数据流App开发模式。

## Inputs

FireEye的升级版，Android 输入校验库。

# License

    Copyright 2015 Yoojia Chen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
