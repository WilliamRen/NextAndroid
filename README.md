# Next Android 库总览

NextAndroid 是一个集成Android App开发工具库

## Events - 事件总线

类似Google Guava的EventBus，NextEvents提供一个事件总线处理库。它提供了线程回调，MainUI回调等非常有用的事件响应处理方式。

在 `Intel i5` / `8G` / `Win10` / `Robolectric` 环境下测试结果：

Schedule类型及负载|发送速率|发送用时|执行负载总用时|调用负载总次数
----|----|----|----|----
SingleThread(1ms Payload)|	 859/s|		8ms|		2326ms|	    2000
CallerThread(1ms Payload)|	 868/s|		2302ms|	    2302ms|	    2000
MultiThreads(1ms Payload)|	 3485/s|	5ms|		573ms|		2000
SingleThread(NopPayload)|	 2000420/s|		999ms|		999ms|		2000000
MultiThreads(NopPayload)|	 1506137/s|		1211ms|	    1327ms|	    2000000
CallerThread(NopPayload)|	 9923440/s|		201ms|		201ms|		2000000

## Click Proxy - 点击代理

基于NextEvents的扩展，一个@ClickEvt注解即快速实现绑定UI组件的点击处理处理。

## Inject View - 自动注入View

View自动注入处理，不再findViewById。

## Flux - 比MVC更清晰的开发模式

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
