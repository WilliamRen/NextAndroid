
**NextInputs** 是 `NextAndroid` 库中的重要子模块，也是Android开发中常用的模块。它基于 FireEye [https://github.com/yoojia/FireEye](https://github.com/yoojia/FireEye) 升级而来，继承了FireEye校验功能，并具有良好的扩展性，你可以根据业务需要创建自定义的校验规则。

NextAndroid的Github地址是：[https://github.com/yoojia/NextAndroid](https://github.com/yoojia/NextAndroid)

NextInputs的Github地址也是：[https://github.com/yoojia/NextAndroid](https://github.com/yoojia/NextAndroid)

# 特性

- 轻量级，自然快;
- 使用简单;
- 内置大量常用校验模式;
- 支持自定义校验模式扩展;
- 支持自定义校验目标扩展;
- 支持自定义错误消息提示;

**NextInputs** 内建了两种校验模式分类：`静态校验模式`、`数值校验模式`。如文章标题所言，使用NextInputs能够极大地提升Android App的开发效率，内置的多个校验模式已能应付App开发常用的校验需求，如有特殊校验需求，通过扩展接口也能快速解决。

# 使用示例

```java
NextInputs inputs = new NextInputs();
inputs.add(Inputs.editText(mUsernameEditText),
        StaticPattern.Required(),
        StaticPattern.Email()
);
boolean passed = inputs.test();
```

如例子所示，**NextInputs**在使用时，创建其实例即可使用。将各个需要校验目标组件EditText、CheckBox等组件，使用Inputs的包装方法，如`Inputs.editText()`、`Inputs.checkBox()`包装，再用 StaticPattern.xx 指定内置校验模式，即完成校验配置。

# 配置 Gradle 依赖

```groovy
dependencies {
    compile 'com.github.yoojia:next-android:<latest-version>@aar'
}
```

# StaticPattern - 静态校验模式

`静态校验模式`也可以称为数据格式匹配模式。在校验时不需要其它辅助参数，而是直接校验输入数据的格式是否符合要求。NextInputs目前内置包含以下几种静态校验模式，在未来版本也会加入其它使用频率较高的模式：

- Required - 必填模式
- NotBlank - 非空模式
- Digits - 数字模式
- Email - 邮件地址模式
- IPv4 - IP地址模式
- Host - 域名模式
- URL - URL地址模式
- Numeric - 数值模式
- BlankCard - 银行卡/信用卡号码模式
- ChineseIDCard 身份证号码模式
- ChineseMobile 手机号码模式（国内手机号）
- IsTrue - 结果为True模式
- IsFalse - 结果为False模式

## Required - 必填模式

> 设置`Required`模式后，输入内容不能为空，也是NextInputs不允许空值的两条校验规则之一

在设置Required模式后，Required模式在校验时会优先于其它模式。其它校验模式在默认情况下将按代码添加顺序依次执行。

注意：

- Required的校验优先级默认为 `StaticPattern/ValuesPattern.PRIORITY_REQUIRED(-1024)`，其它优先级为 `StaticPattern/ValuesPattern.PRIORITY_GENERAL(0)`。
- 如果输入条目没有添加Required校验模式（或者NotBlank模式），在输入内容为空的情况下，校验结果是通过（Passed）的。

## NotBlank - 非空数据模式

> 设置Required模式后，输入内容不能是任何空值，也是NextInputs不允许空值的两条校验规则之一

`StaticPattern.Required()`，输入内容不能为任何空数据，除空数据外，还包括任意数量的纯空格、制表符等。

## Digits - 数字模式

`StaticPattern.Digits()`，输入内容只能是纯数字。

## Email - 邮件地址模式

`StaticPattern.Email()`，输入内容必须是有效的电子邮件地址。

## IPv4 - IP地址模式

`StaticPattern.IPv4()`，输入内容必须是有效的IPv4地址。

## Host - 主机地址模式

`StaticPattern.Host()`，输入内容必须是有效的主机地址。这个模块包含了IPv4的校验。

## URL - URL地址模式

`StaticPattern.URL()`，输入内容必须是有效的URL地址。

## Numeric - 数值模式

`StaticPattern.Numeric()`，输入内容必须是有效的数值。

## BlankCard - 银行卡/信用卡号码模式

`StaticPattern.BlankCard()`，输入内容必须是有效的银行卡号或者信用卡号。这个模式在实现上，使用银行卡号校验算法（Luhn）来校验，可以支持13位到19位长度的有效卡号。

## ChineseIDCard 身份证号码模式

`StaticPattern.ChineseIDCard()`，输入内容必须是有效的中国居民身份证号码。这个模式支持15位和18位身份证号。

## ChineseMobile 手机号码模式（国内手机号）

`StaticPattern.ChineseMobile()`，输入内容必须是有效的手机号。这个手机号必须是11位国内手机号，其它国家或者地区的手机号暂不支持。

## IsTrue - 结果为True模式

`StaticPattern.IsTrue()`，输入内容必须是True值。通常用于校验RadioBotton或者CheckBox等。

## IsFalse - 结果为False模式

`StaticPattern.IsFalse()`，输入内容必须是False值。通常用于校验RadioBotton或者CheckBox等。

# ValuesPattern - 数值校验模式

数值校验模式需要指定校验参数来完成校验。

## Required -  必填项目

`ValuesPattern.Required()`，与StaticPattern.Required相同。

## MinLength - 最小内容长度

`ValuesPattern.MinLength(minLength)`，输入内容的长度至少（包括）为指定长度。

## MaxLength - 最多内容长度

`ValuesPattern.MaxLength(maxLength)`，输入内容的长度最大（包括）为指定长度。

## RangeLength - 内容长度在指定范围内

`ValuesPattern.RangeLength(minLength, maxLength)`，输入内容的长度在指定范围内。

## MinValue - 最小值

`ValuesPattern.MinValue(minValue)`，输入数值最小（包括）为指定数值。支持的数值类型：Int、Long、Float、Double；

## MaxValue - 最大值

`ValuesPattern.MaxValue(minValue)`，输入数值最大（包括）为指定数值。支持的数值类型：Int、Long、Float、Double；

## RangeValue - 数值范围

`ValuesPattern.RangeValue(minValue, maxValue)`，输入数值在（包括）指定数值范围内。支持的数值类型：Int、Long、Float、Double；

## EqualsTo - 与指定内容相同

`ValuesPattern.EqualsTo(...)`，输入内容与指定内容相同。EqualsTo有两个实现方式，一个是`EqualsTo(Loader<String>)`，另一个是`EqualsTo(fixedString)`；

- `EqualsTo(Loader<String>)`，延迟加载参数形式。当开始校验时，Loader的`onLoadValue`方法才会被执行，并要求返回一个用于匹配输入内容的字符串；
- `EqualsTo(fixedString)`，与指定固定字符串相同；

## NotEqualsTo - 与指定内容不相同

方式与`ValuesPattern.EqualsTo`相同，判断方式取反。

# 设置校验失败提示消息

NextInputs内置的所有校验模式都自带提示消息，这些提示消息描述也比较“抽象”，很可能不符合你的业务需要。使用 `.msgOnFail(String)` 接口或者 `.msg(String)` 接口可以设置校验失败提示消息。当校验失败时，提示消息将通过MessageDisplay接口的具体实现类处理并显示出来。

这两个接口的作用是相同的，如果你觉得`.msg(String)`会导致你突然忘记这方法是干哈的，你可以使用`.msgOnFail(String)`方法来提醒自己。

在处理校验失败提示时，NextInputs默认的MessageDisplay实现为：

- 如果的TextView及其子类，使用TextView.setError()方法显示提示信息；
- 如果是其它组件，使用Toast来显示出错信息；

使用 `NextInputs.setMessageDisplay(MessageDisplay)` 方法可以覆盖默认实现，使用你想要的校验失败提示方式。

# 自定义校验优先级

NextInputs是通过 `Pattern.priority` 的数值来决定优先级的，按数值升序。在非常有必要的情况下，可以使用 `Pattern.setPriority(int)` 方法来自定义校验模式的优先级。不建议自定义优先级，使用代码顺序即可。

# Input和Inputs

NextInputs 在框架设计上，将所有输入抽象为 `Input` 类。它有一个异步读取输入内容的接口 `String value()`，在执行校验过程时，NextInputs通过调用此接口来读取需要校验的内容。

通过实现`Input`类，你可以根据业务需要扩展自定义的输入。

`Inputs`是一个辅助工具类，包装实现了Android多个组件的Input实现。包括`EditText`、`RadioButton`、`CheckBox`等常用的需要校验的组件。在`Inputs.textView(TextView view)`接口实现中可以看到，以TextView为校验输入组件的Input实现类是一个匿名类，它仅仅是在value()接口中返回TextView.getText()的结果。

# 空值

NextInputs对输入数据空值字符串的定义是：

```
input == null || input.length() == 0
```