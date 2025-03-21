JVM专题

### 计算机体系结构

> **JVM的设计实际上遵循了遵循冯诺依曼计算机结构**

![image-20240115212347414](E:\图灵课堂\JVM\JVM专题.assets\image-20240115212347414.png)

### CPU与内存交互图：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/30e310b905704b8cbdcc42111a9c905f.png)

### 硬件一致性协议：

MSI、MESI、MOSI、Synapse、Firely、DragonProtocol

### 摩尔定律

摩尔定律是由英特尔(Intel)创始人之一戈登·摩尔(Gordon Moore)提出来的。其内容为：当价格不变时，集成电路上可容纳的晶体管数目，约每隔18个月便会增加一倍，性能也将提升一倍。换言之，每一美元所能买到的电脑性能，将每隔18个月翻两倍以上。这一定律揭示了信息技术进步的速度。

> 为了使摩尔定律更为准确，在摩尔定律发现后10年，1975年的时候，摩尔又做了一些修改。将翻番的时间从一年半调整为两年。

**计算机处理数据过程**

1）提取阶段:由输入设备把原始数据或信息输入给计算机存储器存起来

（2）解码阶段:根据CPU的指令集架构(ISA)定义将数值解译为指令

（3）执行阶段:再由控制器把需要处理或计算的数据调入运算器

（4）最终阶段:由输出设备把最后运算结果输出

### **JVM是什么？**

Java Virtual Machine(Java虚拟机)

**Write Once Run Anywhere**

Java官网：https://docs.oracle.com/javase/8/

Reference -> Developer Guides -> 定位到:https://docs.oracle.com/javase/8/docs/index.html

![image-20240115213135563](E:\图灵课堂\JVM\JVM专题.assets\image-20240115213135563.png)



![image-20240115213211205](E:\图灵课堂\JVM\JVM专题.assets\image-20240115213211205.png)



(1)源码到类文件

(2)类文件到JVM

(3)JVM各种折腾[内部结构、执行方式、垃圾回收、本地调用等]

![image-20240115213322379](E:\图灵课堂\JVM\JVM专题.assets\image-20240115213322379.png)

https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html

**Simple analysis**

u4 :cafebabe

u2+u2:0000+0034，34等于10进制的52，表示JDK8

u2:003f=63(10进制)

**表示常量池中的数量是****62**

cp_info constant_pool[constant_pool_count-1]

**常量池主要存储两方面内容：字面量****(Literal)****和符号引用****(Symbolic References)**

u2 constant_pool_count;

cp_info constant_pool[constant_pool_count-1];

u2 access_flags;

u2 this_class;

u2 super_class;

u2 interfaces_count;

u2 interfaces[interfaces_count];

u2 fields_count;

field_info fields[fields_count];

u2 methods_count;

method_info methods[methods_count];

u2 attributes_count;

attribute_info attributes[attributes_count];

}

magic:The magic item supplies the magic number identifying the class file

format

minor_version

major_version

constant_pool_count:

The value of the constant_pool_count item is equal to the number of entries

in the constant_pool table plus one.

The constant_pool is a table of structures representing various string

constants, class and interface names, field names, and other constants that

are referred to within the ClassFile structure and its substructures. The

format of each constant_pool table entry is indicated by its first "tag"

byte.

The constant_pool table is indexed from 1 to constant_pool_count - 1.

字面量:文本字符串，final修饰等

符号引用：类和接口的全限定名、字段名称和描述符、方法名称和描述符**2.1.3.4 javap****验证**

JDK自带的命令

javap -h

可以验证一下上述Classfile Structure前面几块内容的正确性

javap -v -p Person.class 进行反编译，查看字节码信息和指令等信息

是否有一种感觉？

JVM相对class文件来说可以理解为是操作系统；class文件相对JVM来说可以理解为是汇编语言或者机器

语言

**Continous analysis**

上面分析到常量池中常量的数量是62，接下来我们来具体分析一下这62个常量

cp_info constant_pool[constant_pool_count-1] 也就是这块包括的信息

cp_info其实就是一个表格的形式

All constant_pool table entries have the following general format:

官网 ：https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4

cp_info {

u1 tag;

u1 info[];

}(1)往下数一个u1，即0a->10:代表的是CONSTANT_Methodref，表示这是一个方法引用

CONSTANT_Fieldref_info {

u1 tag;

u2 class_index;

u2 name_and_type_index;

}

往下数u2和u2

u2，即00 0a->10:代表的是class_index，表示该方法所属的类在常量池中的索引

u2，即00 2b->43:代表的是name_and_type_index，表示该方法的名称和类型的索引

\#1 = Methodref #10，#43

(2)往下数u1，即08->8:表示的是CONSTANT_String，表示字符串类型往下数u2

u2，即00 2c->44:代表的是string_index

(3)往下数u1，即09->9:表示的是CONSTANT_Fieldref，表示字段类型

往下数u2和u2

u2，即00 0d->13:代表的是class_index

u2，即00 2d->45:代表的是name_and_type_index

**2.2** 类文件到虚拟机(类加载机制)

**所谓类加载机制就是**

**2.2.1 **装载(Load)

查找和导入class文件

（1）通过一个类的全限定名获取定义此类的二进制字节流

（2）将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构

（3）在Java堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入口

CONSTANT_String_info {

u1 tag;

u2 string_index;

}

\#1 = Methodref #10，#43

\#2 = String #44

CONSTANT_Fieldref_info {

u1 tag;

u2 class_index;

u2 name_and_type_index;

}

\#1 = Methodref #10.#43

\#2 = String #44

\#3 = Fieldref #13.#45

虚拟机把Class文件加载到内存

并对数据进行校验，转换解析和初始化

形成可以虚拟机直接使用的Java类型，即java.lang.Class

**链接(Link)**

**2.2.2.1** **验证(Verify)**

保证被加载类的正确性

文件格式验证

元数据验证

字节码验证

符号引用验证

**2.2.2.2** **准备(Prepare)**

为类的静态变量分配内存，并将其初始化为默认值

Class对象封装了类在方法区内的数据结构，并且向Java程序员提供了访问方法区内的数据结构的接口。在

Java堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入口



```java
public class Demo1 {

    private static int i;

    public static void main(String[] args) {

        // 正常打印出0，因为静态变量i在准备阶段会有默认值0

        System.out.println(i);

    }

}
```

**2.2.2.3** **解析(Resolve)**

把类中的符号引用转换为直接引用

**2.2.3** **初始化(Initialize)**

对类的静态变量，静态代码块执行初始化操作

**2.2.4** **类加载器ClassLoader**

在装载(Load)阶段，其中第(1)步:通过类的全限定名获取其定义的二进制字节流，需要借助类装载

器完成，顾名思义，就是用来装载Class文件的。

**2.2.4.1** **分类**



```java
public class Demo2 {

	public static void main(String[] args) {

        // 编译通不过，因为局部变量没有赋值不能被使用

        int i;

        System.out.println(i);

    }

}
```

符号引用就是一组符号来描述目标，可以是任何字面量。

直接引用就是直接指向目标的指针、相对偏移量或一个间接定位到目标的句柄。

解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程。

解析动作主要针对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用限定符7类符号引用进

行。

1）Bootstrap ClassLoader 负责加载$JAVA_HOME中 jre/lib/rt.jar 里所有的class或

Xbootclassoath选项指定的jar包。由C++实现，不是ClassLoader子类。

2）Extension ClassLoader 负责加载java平台中扩展功能的一些jar包，包括$JAVA_HOME中

jre/lib/*.jar 或 -Djava.ext.dirs指定目录下的jar包。

3）App ClassLoader 负责加载classpath中指定的jar包及 Djava.class.path 所指定目录下的类和

jar包。

4）Custom ClassLoader 通过java.lang.ClassLoader的子类自定义加载class，属于应用程序根据自

身需要自定义的ClassLoader，如tomcat、jboss都会根据j2ee规范自行实现ClassLoader。



### 常量池分类：

#### 1.静态常量池

静态常量池是相对于运行时常量池来说的，属于描述class文件结构的一部分

由**字面量**和**符号引用**组成，在类被加载后会将静态常量池加载到内存中也就是运行时常量池

**字面量** ：文本，字符串以及Final修饰的内容

**符号引用** ：类，接口，方法，字段等相关的描述信息。

#### 2.运行时常量池

当静态常量池被加载到内存后就会变成运行时常量池。

> 也就是真正的把文件的内容落地到JVM内存了

#### 3.字符串常量池

**设计理念：**字符串作为最常用的数据类型，为减小内存的开销，专门为其开辟了一块内存区域（字符串常量池）用以存放。

JDK1.6及之前版本，字符串常量池是位于永久代（相当于现在的方法区）。

JDK1.7之后，字符串常量池位于Heap堆中

**面试常问点：（笔试居多）**

下列三种操作最多产生哪些对象

**1.直接赋值**

`String a ="aaaa";`

解析：

最多创建一个字符串对象。

首先“aaaa”会被认为字面量，先在字符串常量池中查找（.equals()）,如果没有找到，在堆中创建“aaaa”字符串对象，并且将“aaaa”的引用维护到字符串常量池中（实际是一个hashTable结构，存放key-value结构数据），再返回该引用；如果在字符串常量池中已经存在“aaaa”的引用，直接返回该引用。

**2.new String()**

`String  a  =new  String("aaaa");`

解析：

最多会创建两个对象。

首先“aaaa”会被认为字面量，先在字符串常量池中查找（.equals()）,如果没有找到，在堆中创建“aaaa”字符串对象，然后再在堆中创建一个“aaaa”对象，返回后面“aaaa”的引用；
**3.intern()**

```java
String s1 = new String("yzt");
String s2 = s1.intern();
System.out.println(s1 == s2); //false
```

解析：

String中的intern方法是一个 native 的方法，当调用 intern方法时，如果常量池已经包含一个等于此String对象的字符串（用equals(object)方法确定），则返回池中的字符串。否则，将intern返回的引用指向当前字符串 s1(jdk1.6版本需要将s1 复制到字符串常量池里)

常量池在内存中的布局：

)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/05f6a433465e4d77b67c5817e3ab0381.png)



### **加载原则[双亲委派]**

（1）检查某个类是否已经加载

自底向上，从Custom ClassLoader到BootStrap ClassLoader逐层检查，只要某个Classloader已加载，

就视为已加载此类，保证此类只所有ClassLoader加载一次。

（2）加载的顺序

自顶向下，也就是由上层来逐层尝试加载此类

双亲机制可以

- 避免类的重复加载
- 保护程序安全，防止核心API被随意篡改
  - 自定义类：java.lang.String
  - 自定义类：java.lang.ShkStart（报错：阻止创建 java.lang开头的类）



### 打破双亲委派的典范：

数据库框架JDBC Driver    JDBC4.0使用SPI机制打破双亲委派

![image-20200714171918988](http://qiniu.seaxiang.com//1660212074-OQUfC0XM.png)

Web容器Tomcat/Jboss

![image-20240115222320040](E:\图灵课堂\JVM\JVM专题.assets\image-20240115222320040.png)

### 为什么要打破双亲委派？

以tomcat为例：是一个web容器，主要是需要解决以下问题

- 一个web容器可能要部署两个或多个应用程序，不同的应用程序之间可能会依赖同一个第三方类库的不同版本，因此要保证每个应用程序的类库都是独立的、相互隔离的
- 部署在同一个web容器中的相同类库的相同版本可以共享，否则，会有重复的类库被加载进JVM中
- web容器也有自己的类库，不能和应用程序的类库混淆，需要相互隔离
- web容器支持jsp文件修改后不用重启，jsp文件也要编译成.class文件的，支持HotSwap功能

**Tomcat使用Java默认加载器的问题**

默认的类加载器无法加载两个相同类库的不同版本，它只在乎类的全限定类名，并且只有一份，所以无法解决上面的问题1和问题3，也就是相关隔离的问题。

同时在修改jsp文件后，因为类名一样，默认的类加载器不会重新加载，而是使用方法区中已经存在的类，所以需要每个jsp对应一个唯一的类加载器，当修改jsp的时候，直接卸载唯一的类加载器，然后重新创建类加载器，并加载jsp文件。

tomcat会为每一个jsp生成一个类加载器. 这样每个类加载器都加载自己的jsp, 不会加载别人的. 当jsp文件内容修改时, tomcat会有一个监听程序来监听jsp的改动. 比如文件夹的修改时间, 一旦时间变了, 就重新加载文件夹中的内容. 

### 打破双亲委派的方法：

重写loadClass方法，SPI机制，OSGI按模块热部署打破双亲委派

1.自定义类加载器

    当两个类的全限定名相同时，就不可以同时加载，入tomcat运行两个web服务时就会出现冲突（相同类名），不够tomcat通过为每一个应用分配一个应用类加载器，打破双亲委派机制。
自定义实现
（1）类继承ClassLoader类，重写loadClass方法
（2）loadClass中获取被加载类数据（loadClassData）
（3）调用defineClass完成类加载
注：加载类的时候会要求所有类都继承父类（默认object类），不然会报错，可以将待加载类同目录放入父类，也可直接将父类交由ClassLoader中的类加载器加载，在一个Java虚拟机中，只有同一个类加载器外加相同的类限定名才会认为冲突


2.线程上下文类加载器（spi机制）

（1）在classPath路径下的META-INF/services文件夹中，以接口全限定名来约定文件名（同时写入文件），对应文件里面写该接口实现（比如mysql驱动）
（2）serviceLoader加载实现类
注：实际使用的类加载器是线程上下文中的类加载器（application类加载器），可通过Thread.currentThread().getContextClassLoader()获取，也可以手动设置自定义类加载器，这种方式在驱动的加载中任然依赖于双亲委派机制
完整流程：bootstrap加载DriverManager，再通过DriverManger通过spi机制加载其他类

3.osgi框架类加载器（已不在使用）
自己实现了一套加载机制，实现了类的热部署，现在可用arthas实现
打破双亲委派机制的最简单的方法就是重写ClassLoader





## 运行时数据区(Run-Time Data Areas)

> ```
> 在装载阶段的第(2),(3)步可以发现有运行时数据，堆，方法区等名词
> (2)将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构
> (3)在Java堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入口
> 说白了就是类文件被类装载器装载进来之后，类中的内容(比如变量，常量，方法，对象等这些数据得要有个去处，也就是要存储起来，存储的位置肯定是在JVM中有对应的空间)
> ```

定义：在执行java程序的过程中会把它所管理的内存划分成若干个不同的数据区域。

主要包括：

线程私有的：程序计数器，java虚拟机栈，本地方法栈

线程共享的：堆内存，方法区（包括运行时常量池）

### 2.3.1 官网概括

`官网`：[https://docs.oracle.com/javase/specs/jvms/se8/html/index.html](https://docs.oracle.com/javase/specs/jvms/se8/html/index.html)

```
The Java Virtual Machine defines various run-time data areas that are used during execution of a program. Some of these data areas are created on Java Virtual Machine start-up and are destroyed only when the Java Virtual Machine exits. Other data areas are per thread. Per-thread data areas are created when a thread is created and destroyed when the thread exits.
```

### 2.3.2 图解

```
Each run-time constant pool is allocated from the Java Virtual Machine's method area (§2.5.4).s
```

![img](https://pics3.baidu.com/feed/48540923dd54564e6401f2a939d79c8bd0584fc8.png@f_auto?token=0aff26b9778e49619f85474c29b63b1c)

2.3.3 初步认识

#### 2.3.3.1 Method Area(方法区)

**（1）方法区是各个线程共享的内存区域，在虚拟机启动时创建**

```
The Java Virtual Machine has a method area that is shared among all Java Virtual Machine threads. 
The method area is created on virtual machine start-up. 
```

**（2）虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却又一个别名叫做Non-Heap(非堆)，目的是与Java堆区分开来**

```
Although the method area is logically part of the heap,......
```

**（3）用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据**

```
It stores per-class structures such as the run-time constant pool, field and method data, and the code for methods and constructors, including the special methods (§2.9) used in class and instance initialization and interface initialization.
```

**（4）当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常**

```
If memory in the method area cannot be made available to satisfy an allocation request, the Java Virtual Machine throws an OutOfMemoryError.
```

> **此时回看装载阶段的第2步，将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构**
>
> **如果这时候把从Class文件到装载的第(1)和(2)步合并起来理解的话，可以画个图**

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/470623d0d410487baa6ec930ffccf3a3.png)

> **值得说明的**
>
> ```
> JVM运行时数据区是一种规范，真正的实现
> 在JDK 8中就是Metaspace，在JDK6或7中就是Perm Space
> ```

#### 2.3.3.2 Heap(堆)

**（1）Java堆是Java虚拟机所管理内存中最大的一块，在虚拟机启动时创建，被所有线程共享。**

**（2）Java对象实例以及数组都在堆上分配。**

```
The Java Virtual Machine has a heap that is shared among all Java Virtual Machine threads. The heap is the run-time data area from which memory for all class instances and arrays is allocated.
The heap is created on virtual machine start-up.
```

> **此时回看装载阶段的第3步，在Java堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入口**

`此时装载(1)(2)(3)的图可以改动一下`

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/4ebfb823f2544f24b8f6ea1e6edb9a7b.png)

#### 2.3.3.3 Java Virtual Machine Stacks(虚拟机栈)

> **经过上面的分析，类加载机制的装载过程已经完成，后续的链接，初始化也会相应的生效。**
>
> 假如目前的阶段是初始化完成了，后续做啥呢？肯定是Use使用咯，不用的话这样折腾来折腾去有什么意义？那怎样才能被使用到？换句话说里面内容怎样才能被执行？比如通过主函数main调用其他方法，这种方式实际上是main线程执行之后调用的方法，即要想使用里面的各种内容，得要以线程为单位，执行相应的方法才行。
>
> 那一个线程执行的状态如何维护？一个线程可以执行多少个方法？这样的关系怎么维护呢？

**（1）虚拟机栈是一个线程执行的区域，保存着一个线程中方法的调用状态。**

**换句话说，一个Java线程的运行状态，由一个虚拟机栈来保存，所以虚拟机栈肯定是线程私有的，独有的，随着线程的创建而创建。**

```
Each Java Virtual Machine thread has a private Java Virtual Machine stack, created at the same time as the thread.
```

**（2）每一个被线程执行的方法，为该栈中的栈帧，即每个方法对应一个栈帧。**

**调用一个方法，就会向栈中压入一个栈帧；一个方法调用完成，就会把该栈帧从栈中弹出。**

```
 A Java Virtual Machine stack stores frames (§2.6). 
```

```
A new frame is created each time a method is invoked. A frame is destroyed when its method invocation completes.
```

* **图解栈和栈帧**

  

```java
void a(){
	b();
}
void b(){
	c();
}
void c(){

}
```

加载过程如下图：只有执行过程中才能确定最终执行的方法。由动态链接让符号引用转为直接引用。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/04f42e24150f4fc3b0081dde6de41a5b.png)

* **栈帧**

`官网`：[https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.6](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.6)

**栈帧：每个栈帧对应一个被调用的方法，可以理解为一个方法的运行空间。**

**每个栈帧中包括局部变量表(Local Variables)保存方法的局部变量、**

**操作数栈(Operand Stack)JVM 使用一种基于栈的指令集，在执行运算时，是在操作数栈上执行的、**

**动态链接(Dynamic Linking)指向运行时常量池的引用(A reference to the run-time constant pool)、**

**方法返回地址(Return Address)和附加信息。**

```
局部变量表:方法中定义的局部变量以及方法的参数存放在这张表中
局部变量表中的变量不可直接使用，如需要使用的话，必须通过相关指令将其加载至操作数栈中作为操作数使用。
```

```
操作数栈:以压栈和出栈的方式存储操作数的
```

```
动态链接:每个栈帧都包含一个指向运行时常量池中该栈帧所属方法的引用，持有这个引用是为了支持方法调用过程中的动态连接(Dynamic Linking)。
```

```
方法返回地址:当一个方法开始执行后,只有两种方式可以退出，一种是遇到方法返回的字节码指令；一种是遇见异常，并且这个异常没有在方法体内得到处理。
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/14546deb17964eb2887b8145280c59aa.png)

* **结合字节码指令理解栈帧**

> **javap -c Person.class > Person.txt**

```
Compiled from "Person.java"
class Person {
...     
 public static int calc(int, int);
  Code:
   0: iconst_3   //将int类型常量3压入[操作数栈]
   1: istore_0   //将int类型值存入[局部变量0]
   2: iload_0    //从[局部变量0]中装载int类型值入栈
   3: iload_1    //从[局部变量1]中装载int类型值入栈
   4: iadd     //将栈顶元素弹出栈，执行int类型的加法，结果入栈
   5: istore_2   //将栈顶int类型值保存到[局部变量2]中
   6: iload_2    //从[局部变量2]中装载int类型值入栈
   7: ireturn    //从方法中返回int类型的数据
...
}
```



```
On class method invocation, any parameters are passed in consecutive local variables starting from local variable 0. On instance method invocation, local variable 0 is always used to pass a reference to the object on which the instance method is being invoked (this in the Java programming language). Any parameters are subsequently passed in consecutive local variables starting from local variable 1.
```



#### 2.3.3.4 The pc Register(程序计数器)

> **我们都知道一个JVM进程中有多个线程在执行，而线程中的内容是否能够拥有执行权，是根据CPU调度来的。**
>
> **假如线程A正在执行到某个地方，突然失去了CPU的执行权，切换到线程B了，然后当线程A再获得CPU执行权的时候，怎么能继续执行呢？这就是需要在线程中维护一个变量，记录线程执行到的位置。**

- 1、JVM支持多个线程同时运行，每个线程拥有一个程序计数器，是线程私有的，用来存储指向下一条指令的地址。
- 2、在创建线程的时候，创建相应的程序计数器。
- 3、执行本地native方法时，程序计数器的值为undefined。
- 4、是一块比较小的内存空间，是唯一一个在JVM规范中没有规定OutOfMemoryError的内存区域。

**如果线程正在执行Java方法，则计数器记录的是正在执行的虚拟机字节码指令的地址；**

**如果正在执行的是Native方法，则这个计数器为空。**

```
The Java Virtual Machine can support many threads of execution at once (JLS §17). Each Java Virtual Machine thread has its own pc (program counter) register. At any point, each Java Virtual Machine thread is executing the code of a single method, namely the current method (§2.6) for that thread. If that method is not native, the pc register contains the address of the Java Virtual Machine instruction currently being executed. If the method currently being executed by the thread is native, the value of the Java Virtual Machine's pc register is undefined. The Java Virtual Machine's pc register is wide enough to hold a returnAddress or a native pointer on the specific platform.
```

#### 2.3.3.5 Native Method Stacks(本地方法栈)

**如果当前线程执行的方法是Native类型的，这些方法就会在本地方法栈中执行。**

**那如果在Java方法执行的时候调用native的方法呢？**

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/7e7675ab8eca4fa9a73f80b4ccf568fe.png)

#### 栈指向堆

**如果在栈帧中有一个变量，类型为引用类型，比如Object obj=new Object()，这时候就是典型的栈中元**
**素指向堆中的对象。**

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/7c9fa5aec3954a3bb557ed1e9c8b5aaf.png)

#### 2.3.4.2 方法区指向堆

**方法区中会存放静态变量，常量等数据。如果是下面这种情况，就是典型的方法区中元素指向堆中的对**
**象。**

```
private static Object obj=new Object();
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/503d6384d1e344d2b09ddda80e5f069b.png)

#### 2.3.4.3 堆指向方法区

**What？堆还能指向方法区？**
**注意，方法区中会包含类的信息，堆中会有对象，那怎么知道对象是哪个类创建的呢？**

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1648646330029/84516da79a784372b5bb7a0359cbd787.png)

`思考`：

**一个对象怎么知道它是由哪个类创建出来的？怎么记录？这就需要了解一个Java对象的具体信息咯。**



## JVM为什么使用元空间替换了永久代

JDK8 之前可以通过 `-XX:PermSize` 和 `-XX:MaxPermSize` 来设置永久代大小，

先看下JDK7内存模型：

![img](E:\图灵课堂\JVM\JVM专题.assets\image-20240116212355719.png)

JDK 1.8 及以后：无永久代，使用元空间（存放在本地内存中）实现方法区，常量保存在元空间，但字符串常量池和静态变量依然保存在堆中。

永久代和元空间都是 HotSpot 虚拟机中的概念，HotSpot 虚拟机是 Sun JDK 和 Open JDK 中自带的虚拟机，也是目前使用范围最广泛的 Java 虚拟机，当我们提到虚拟机时，大概率指的就是 HotSpot 虚拟机。

![image-20240116211143159](E:\图灵课堂\JVM\JVM专题.assets\image-20240116211452629.png)

但从《Java 虚拟机规范》的层面来说，并没有所谓的“永久代”和“元空间”等区域

详见官方文档：https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-2.html#jvms-2.5。

《Java 虚拟机规范》只是规定了一个区域叫“方法区（Method Area）”，而“永久代”和“元空间”是 HotSpot 虚拟机在不同的 JDK 版本下，对方法区的具体实现而已。这就好像，世界羽协规定羽毛球比赛必须要使用羽毛球拍（方法区），而中国羽毛球运动员，第一年使用的是红双喜牌的羽毛球拍（永久代），第二年使用的是李宁牌羽毛球拍（元空间）一样。

**方法区与永久代，元空间之间的关系**
方法区是一种规范，不同的虚拟机厂商可以基于规范做出不同的实现，永久代和元空间就是出于不同jdk版本的实现。

说白了，方法区就像是一个接口，永久代与元空间分别是两个不同的实现类而已。只不过永久代是这个接口最初的实现类，后来这个接口一直进行变更，直到最后彻底废弃这个实现类，由新实现类——元空间进行替代。 

那么问题来了，

**永久代为什么被元空间给替代了**？

## 1.官方答案

关于这个问题，官方在 JEP 122: Remove the Permanent Generation（移除永久代）中给出了答案，原文内容如下：

Motivation（动机）

This is part of the JRockit and Hotspot convergence effort. JRockit customers do not need to configure the permanent generation (since JRockit does not have a permanent generation) and are accustomed to not configuring the permanent generation.

以上内容翻译成中文大意是：

这是 JRockit 虚拟机和 HotSpot 虚拟机融合工作的一部分。JRockit 客户不需要配置永久层代（因为 JRockit 没有永久代），所以要移除永久代。

JRockit 是 Java 官方收购的一家号称史上运行最快的 Java 虚拟机厂商，之后 Java 官方在 JDK 8 时将 JRockit 虚拟机和 HotSpot 虚拟机进行了整合。

PS：JEP 是 JDK Enhancement Proposal 的缩写，翻译成中文是 JDK 改进提案。你也可以把它理解为 JDK 的更新文档。

通过官方的描述，我们似乎找到了答案，也就是说，**之所以要取消“永久代”是因为 Java 官方收购了 JRockit，之后在将 JRockit 和 HotSpot 进行整合时，因为 JRockit 中没有“永久代”，所以把永久代给移除了。**

PS：上面的那段描述好像说的已经很清楚了，但又好像什么也没说。这就好比，我问你“为什么要买车？”，你说“别人都买车了，所以我要买车”，但为什么别人要买车？

## 2.背后的原因

上述给出了移除永久代的回答，但却没有给出背后的原因，那接下来我们就来讨论一下，为什么要移除永久代？以及为什么要有元空间？

## 2.1 降低 OOM

当使用永久代实现方法区时，永久代的最大容量受制于 PermSize 和 MaxPermSize 参数设置的大小，而这两个参数的大小又很难确定，因为在程序运行时需要加载多少类是很难估算的，如果这两个参数设置的过小就会频繁的触发 FullGC 和导致 OOM（Out of Memory，内存溢出）。

在之前的版本中，字符串常量池存在于永久代中，在大量使用字符串的情况下，非常容易出现OOM的异常。此外，**JVM加载的class的总数，方法的大小**等都很难确定，因此对永久代大小的指定难以确定。太小的永久代容易导致永久代内存溢出，太大的永久代则容易导致虚拟机内存紧张

但是，当使用元空间替代了永久代之后，出现 OOM 的几率就被大大降低了，因为元空间使用的是本地内存，这样元空间的大小就只和本地内存的大小有关了，从而大大降低了 OOM 的问题。

## 2.2 降低运维成本

因为元空间使用的是本地内存，这样就无需运维人员再去专门设置和调整元空间的大小了。

## 3.垃圾回收效率提升

在 HotSpot 虚拟机中，方法区的实现经历了以下 3 个阶段：

1. JDK 1.6 及之前：方法区使用永久代实现，静态变量存放在永久代；

2. JDK 1.7 ：“去永久代”的前置版本，还存在永久代，不过已经将字符串常量池和静态变量从永久代移到了堆上；

3. JDK 1.8 及以后：无永久代，使用元空间（存放在本地内存中）实现方法区，常量保存在元空间，但字符串常量池和静态变量依然保存在堆中。

   永久代是通过FullGC进行垃圾回收，也就是和老年代同时实现垃圾回收，替换元空间后简化了FullGC的过程，可以在不进行暂停的情况下并发的进行垃圾回收，提升了GC的性能。

## 总结

1，永久代虽然可以通过参数设置大小，但JVM加载的class的总数，方法的大小很难确定，因此不好制定其大小。而且用的是JVM内存很容易OOM。

2，使用的是元空间存放在本地内存（上限较大）中的方式来替代永久代的，这样就降低了 OOM 发生的可能性

3，永久代是通过FullGC进行垃圾回收，也就是和老年代同时实现垃圾回收，替换元空间后简化了FullGC的过程，可以在不进行暂停的情况下并发的进行垃圾回收，提升了GC的性能。

4，Oracle合并Hotspot和JRockit，而JRockit没有永久代





# Java对象内存模型 

&#x3e; **一个Java对象在内存中包括3个部分：**对象头( Header )、实例数据( Instance Data ) 和对齐填充( Padding )。


![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/3d172cdad02d4983980efc8e5ca2c6db.png)

### 对象头

如果对象是数组类型，则HotSpot虚拟机用3个字宽（Word）存储对象头，如果对象是非数组类型，则用2字宽存储对象头。在32位虚拟机中，1字宽等于4字节，即32bit；而在64位虚拟机中，1字宽等于8字节，即64bit。

**Mark Word**
用于存储对象自身的运行时数据, 如哈希码(HashCode)、GC分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳等，这部分数据的长度在32位和64位的虚拟机中分别为32bit和64bit。

对象需要存储的运行时数据很多,其实已经超出了32位、 64位BitMap结构所能记录的限度,但是对象头信息是与对象自身定义的数据无关的额外存储成本,考虑到虚拟机的空间效率, Mark Word被设计成一个非固定的数据结构以便在极小的空间内存储尽量多的信息,它会根据对象的状态复用自己的存储空间，即不同的状态存储不同的数据。

**Class Pointer**
Class Pointer，对象头的另外一部分是类型指针,即对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象到底是哪个类的实例。 并不是所有的虚拟机实现都必须在对象数据上保留类型指针，换句话说，查找对象的元数据信息并不一定要经过对象本身。

这部分数据的长度在 32 位和 64 位的虚拟机中分别为 32 bit 和 64 bit。



 Array **Length**
如果对象是一个Java数组，那在对象头中还必须有一块用于记录数组长度的数据，因为虚拟机可以通过普通Java对象的元数据信息确定Java对象的大小，但是从数组的元数据中却无法确定数组的大小。

这部分数据的长度在 32 位和 64 位的虚拟机中分别为 32 bit 和 64 bit。

可以使用-XX:+UseCompressedClassPointers参数对64位虚拟机进行类型指针压缩，压缩后长度为32bit。

### 实例数据(Instance Data)

实例数据部分是对象真正存储的有效信息，也是在程序代码中所定义的各种类型的字段内容。无论是从父类继承下来的,还是在子类中定义的，都需要记录起来。

这部分的存储顺序会受到虚拟机分配策略参数( FieldsAllocationStyle ) 和字段在Java源码中定义顺序的影响。

HotSpot虚拟机默认的分配策略为long/double、int/float、short/char、byte/boolean、 oop( Ordinary Object Pointers ，即引用类型，可压缩)，其中使用/分隔的类型采用定义的先后顺序排序，从分配策略中可以看出,相同宽度的字段总是被分配到一起。另外，会使用内存重排序优化空间使用，即一般如果对象头占用12bytes，那么将会选择小于等于4bytes的类型放在后面尝试补齐4bytes空间。

基本类型和引用类型指针之间以4bytes为步长的对齐填充，当实例数据填充完毕之后，在最后还有一次以8bytes为步长的对齐填充。

在满足上面的前提条件的情况下，在父类中定义的变量会出现在子类之前。如果CompactFields参数值为true ( 默认为true )，那么子类之中较窄的变量也可能会插入到父类变量的空隙之中

### 对齐填充(Padding)

对齐填充并不是必然存在的,也没有特别的含义，它仅仅起着占位符的作用，主要用体提升读取的效率。由于HotSpot VM的自动内存管理系统要求对象起始地址必须是8字节的整数倍,换句话说, 就是对象的大小必须是8字节的整数倍。而对象头部分正好是8字节的倍数(1倍或者2倍 )，因此，当对象实例数据部分没有对齐时,就需要通过对齐填充来补全。

Object o = new Object() 在内存中占用16个字节（开启压缩），其中最后4个是对齐填充； 



数据  内存 --  CPU   寄存器      -127   补码   10000001   -  11111111        32位的处理器

一次能够去处理32个二进制位      4字节的数据      64位操作系统    8字节    2的64次方的寻址空间

指针压缩技术       JDK1.6出现的     开启了指针压缩      什么时候指针压缩会无效  ？？

超过32G指针压缩无效

![16499221260943015043ffy](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1/792c11f8b9044bad9651eac2b0c88bb0.png)

**小端存储** :便于数据之间的类型转换，例如:long类型转换为int类型时，高地址部分的数据可以直接截掉。

**大端存储** :便于数据类型的符号判断，因为最低地址位数据即为符号位，可以直接判断数据的正负号。

&#x3e; java中使用的是大端存储。

### 内存模型设计之–Class Pointer

创建好一个对象之后，当然需要去访问它，那么当我们需要访问一个对象的时候，是如何定位到对象的呢？
目前最主流的访问对象方式有两种：**句柄访问**和**直接指针访问**。

句柄池访问：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/d57189bb02aa4488809eb602b1562793.png)

直接指针访问对象图解:

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/e7f4267aee394abf9d7d99c52c1ee150.png)

**区别:**

**句柄池:**

使用句柄访问对象，会在堆中开辟一块内存作为句柄池，句柄中储存了对象实例数据(属性值结构体) 的内存地址，访问类型数据的内存地址(类信息，方法类型信息)，对象实例数据一般也在heap中开 辟，类型数据一般储存在方法区中。

**优点** :reference存储的是稳定的句柄地址，在对象被移动(垃圾收集时移动对象是非常普遍的行为) 时只会改变句柄中的实例数据指针，而reference本身不需要改变。

**缺点** :增加了一次指针定位的时间开销。

**直接访问:**

(Hot Spot虚拟机采用的方式)直接指针访问方式指reference中直接储存对象在heap中的内存地址，但对应的类型数据访问地址需要 在实例中存储。

**优点** :节省了一次指针定位的开销。

**缺点** :在对象被移动时(如进行GC后的内存重新排列)，reference本身需要被修改

### 内存模型设计之–指针压缩

通常64位JVM消耗的内存会比32位的大1.5倍，这是因为对象指针在64位架构下，长度会翻倍（更宽的寻址）。如果项目从32位虚拟机迁移到64位虚拟机，那么突然增大的内存需求可能会让项目崩溃。

指针压缩的目的：

&#x3e; 1. 为了保证CPU普通对象指针(oop)缓存
&#x3e; 2. 为了减少GC的发生，因为指针不压缩是8字节，这样在64位操作系统的堆上其他资源空间就少了。

-XX:+UseCompressedOops：即普通对象指针压缩（OOP即ordinary object pointer）。该参数默认是开启的，可以使用-XX:-UseCompressedOops关闭。
会被压缩的数据有：每个Class的属性指针（静态成员变量)、每个对象的属性指针、普通对象数组的每个元素指针。
不会被压缩的数据有：指向PermGen的Class对象指针，本地变量，堆栈元素，入参，返回值，NULL指针不会被压缩。
-XX:+UseCompressedClassPointers：即类型指针压缩，即针对klass pointer的指针压缩。使用-XX:+UseCompressedClassPointers开启参数，JDK 1.6 update14之后是默认开启的，可以使用-XX:-UseCompressedClassPointers关闭。
上面两种压缩策略，可以算出来的组合有四种：

-XX:+UseCompressedOops -XX:+UseCompressedClassPointers
-XX:+UseCompressedOops -XX:-UseCompressedClassPointers
-XX:-UseCompressedOops -XX:-UseCompressedClassPointers
-XX:-UseCompressedOops -XX:+UseCompressedClassPointers

但是使用第四种开启策略时却会出现警告：

Java HotSpot™ 64-Bit Server VM warning: UseCompressedClassPointers requires UseCompressedOops

这是因为JVM的限制：

```java
  // UseCompressedOops must be on for UseCompressedClassPointers to be on.
  if (!UseCompressedOops) {
    if (UseCompressedClassPointers) {
      warning("UseCompressedClassPointers requires UseCompressedOops");
    }
    FLAG_SET_DEFAULT(UseCompressedClassPointers, false);
  }
```

实际上，UseCompressedClassPointers参数依赖了UseCompressedOops参数，开启UseCompressedOops参数时，UseCompressedClassPointers参数默认开启，关闭UseCompressedOops时，UseCompressedClassPointers参数同样跟着关闭。

下面来看看具体的压缩和非压缩大小对比：

在32位系统下，存放Class指针的空间大小是4字节，MarkWord是4字节，对象头为8字节。
在64位系统下，存放Class指针的空间大小是8字节，MarkWord是8字节，对象头为16字节。
在64位开启普通对象指针压缩的情况下 -XX:+UseCompressedOops，存放Class指针的空间大小是4字节，MarkWord是8字节，对象头为12字节。

**默认是开启指针压缩的**。
如果对象是数组，那么32位额外增加4个字节,64位额外增加8个字节（压缩后增加4个字节）。



### 内存模型设计之–对齐填充

**为什么要对齐填充**？

字段内存对齐的其中一个原因，是让字段只出现在同一CPU的缓存行中。如果字段不是对齐的，那么就有可能出现跨缓存行的字段。也就是说，该字段的读取可能需要替换两个缓存行，而该字段的存储也会同时污染两个缓存行。这两种情况对程序的执行效率而言都是不利的。其实对其填充的最终目的是为了计算机高效寻址。

对齐填充的意义是 **提高CPU访问数据的效率** ，主要针对会存在**该实例对象数据跨内存地址区域存储**的情况。

例如：在没有对齐填充的情况下，内存地址存放情况如下:

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/bc2b480aee764ef4b4157e1cfdc7dcea.png)

因为处理器只能0x00-0x07，0x08-0x0F这样读取数据，所以当我们想获取这个long型的数据时，处理 器必须要读两次内存，第一次(0x00-0x07)，第二次(0x08-0x0F)，然后将两次的结果才能获得真正的数值。

那么在有对齐填充的情况下，内存地址存放情况是这样的:

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/2942966ff63844ecbeacb8433e57958e.png)

现在处理器只需要直接一次读取(0x08-0x0F)的内存地址就可以获得我们想要的数据了。

当我们的策略为0时，这个时候我们的排序是  基本类型&#x3e;填充字段&#x3e;引用类型

当我们策略为1时，引用类型&#x3e;基本类型&#x3e;填充字段

策略为2时，父类中的引用类型跟子类中的引用类型放在一起  父类采用策略0   子类采用策略1，

这样操作可以降低空间的开销 ，



### jol查看对象内存

jol，即Java Object Layout，是openjdk提供的工具包，可以帮我们在运行时计算某个对象的内存布局以及对象的大小，是非常好的工具。

jol介绍：http://openjdk.java.net/projects/code-tools/jol/

maven依赖：

```
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.9</version>
</dependency>
```

3.1 JVM的信息
首先查看JVM的基本信息：

```java
@Test
public void test1() {
    //返回有关当前 VM 模式的信息详细信息
    System.out.println(VM.current().details());
}
```


本人的计算机输出如下：

```
# Running 64-bit HotSpot VM.  
# Using compressed oop with 3-bit shift. 
# Using compressed klass with 3-bit shift. 
# Objects are 8 bytes aligned.  
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes] 
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
```


解释：

 第一行：表示使用的是64位虚拟机；
第二行：表示启用了普通对象指针压缩，即-XX:+UseCompressedOops。
第三行：表示启用了类型指针压缩，即-XX:+UseCompressedClassPointers开启参数。
第四行：对象的大小必须8bytes对齐。
第五行：表示字段类型的指针长度（bytes），依次为引用句柄（对象指针），byte, boolean, char, short, int, float, double, long类型。
第六行：表示数组类型的指针长度（bytes），依次为引用句柄（对象指针），byte, boolean, char, short, int, float, double, long类型。

3.2 object对象
查看object对象的内存布局，这是一道经典的Java面试题：new Object（）的大小是多少？

```java
@Test
public void test2() {
    //ClassLayout:class的内存内存布局
    //parseInstance:表示解析传入的对象
    //toPrintable:表示转换为一种可输出的格式打印//解析object对象的内存布局
	System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
}
```

输出如下：

java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total


首先解释一下对应的名词的意思：

java.lang.Object object internals：object对象的内部布局；
OFFSET：对象内部的某个偏移量，作为某部分的起始位置。
SIZE：对应的组成部分的大小，单位是bytes。
TYPE DESCRIPTION：该部分的类型说明。
VALUE：字节的具体值。
接下来看看object对象的具体布局和大小：

首先是4bytes的object header，即对象头；
接下来还是4bytes的object header；
接下来还是4bytes的object header；
最后是4bytes的（loss due to the next object alignment），字面意思就是“由于下一个对象对齐而造成的损失”，实际上就是对齐填充，前面说过对象的大小8bytes对齐。

由于object header占用12bytes，因此后面还要需要4bytes的对齐填充,所以一共是16bytes。






## 2.4 JVM内存模型

### 2.4.1 运行时数据区

**上面对运行时数据区描述了很多，其实重点存储数据的是堆和方法区(非堆)，所以内存的设计也着重从这两方面展开(注意这两块区域都是线程共享的)。**

**对于虚拟机栈，本地方法栈，程序计数器都是线程私有的。**

**可以这样理解，JVM运行时数据区是一种规范，而JVM内存模式是对该规范的实现**

### 2.4.2 图形展示

```
一块是非堆区，一块是堆区
堆区分为两大块，一个是Old区，一个是Young区
Young区分为两大块，一个是Survivor区（S0+S1），一块是Eden区
S0和S1一样大，也可以叫From和To
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1649922126094/972614631e384016960713173c1d97ab.png)

![16499221260943012495ffy](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1/890db78d3fcf4dc7b0d488529add6af5.png)

### 2.4.3 对象创建过程

#### 对象创建的基本流程:

1. 类加载检查
    虚拟机遇到一条 new 指令时，首先将去检查这个指令的参数是否能在常量池中定位到一个类的符号引用，并且检查这个
    符号引用代表的类是否已被加载、解析和初始化过。如果没有，那必须先执行相应的类加载过程。（详见类加载器及类加载流程）
    new 指令对应到语言层面上讲是， new 关键词、对象克隆、对象序列化等。并完成静态变量成员变量静态代码块的初始化，这样当初始化完成之后常量池就能去找到对应的类元信息了。

2. 分配内存
    在类加载检查通过后，接下来虚拟机将为新生对象分配内存。对象所需内存的大小在类 加载完成后便可完全确定，为
    对象分配空间的任务等同于把 一块确定大小的内存从 Java 堆中划分出来。
    这个步骤有两个问题：

  如何划分内存。

  在并发情况下， 可能出现正在给对象 A 分配内存，指针还没来得及修改，对象 B 又同时使用了原来的指针来分配内存的
  情况。
  划分内存的方法：
  “ 指针碰撞 ” （ Bump the Pointer ） ( 默认用指针碰撞 )
  如果 Java 堆中内存是绝对规整的，所有用过的内存都放在一边，空闲的内存放在另一边，中间放着一个指针作为分界点
  的指示器，那所分配内存就仅仅是把那个指针向空闲空间那边挪动一段与对象大小相等的距离。
  “ 空闲列表 ” （ Free List ）
  如果 Java 堆中的内存并不是规整的，已使用的内存和空 闲的内存相互交错，那就没有办法简单地进行指针碰撞了，虚拟
  机就必须维护一个列表，记 录上哪些内存块是可用的，在分配的时候从列表中找到一块足够大的空间划分给对象实例，
  并更新列表上的记录
  解决并发问题的方法：
  CAS （ compare and swap ）
  虚拟机采用 CAS 配上失败重试的方式保证更新操作的原子性来对分配内存空间的动作进行同步处理。
  本地线程分配缓冲（ Thread Local Allocation Buffer,TLAB ） 把内存分配的动作按照线程划分在不同的空间之中进行，即每个线程在 Java 堆中预先分配一小块内存。通过 ­XX:+/­
  UseTLAB 参数来设定虚拟机是否使用 TLAB(JVM 会默认开启 ­XX:+ UseTLAB ) ， ­XX:TLABSize 指定 TLAB 大小。

3. 初始化"零值"
    内存分配完成后，虚拟机需要将分配到的内存空间都初始化为零值（不包括对象头）比如int=0，string=null。

   如果使用 TLAB ，这一工作过程也可以提前至 TLAB 分配时进行。这一步操作保证了对象的实例字段在 Java 代码中可以不赋初始值就直接使用，程序能访问到这些字段的数据类型所对应的零值。

4. 设置对象头
    初始化零值之后，虚拟机要对对象进行必要的设置，例如这个对象是哪个类的实例、如何才能找到类的元数据信息、对
    象的哈希码、对象的 GC 分代年龄等信息。这些信息存放在对象的对象头 Object Header 之中。
    在 HotSpot 虚拟机中，对象在内存中存储的布局可以分为 3 块区域：对象头（ Header ）、 实例数据（ Instance Data ）
    和对齐填充（ Padding ）。 HotSpot 虚拟机的对象头包括两部分信息，第一部分用于存储对象自身的运行时数据， 如哈
    希码（ HashCode ）、 GC 分代年龄、锁状态标志、线程持有的锁、偏向线程 ID 、偏向时 间戳等。对象头的另外一部分
    是类型指针，即对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例。

5. 执行 <init> 方法,即对象按照程序员的意愿进行初始化。对应到语言层面上讲，就是为属性赋值（注意，这与上面的赋
    零值不同，这是由程序员赋的值），和执行构造方法。对象大小与指针压缩对象大小可以用 jol­core 包查看。init方法是java文件编译后在字节码里面生成的，是一个实例构造器。构造器里会把构造块变量初始化并调用父类构造器等一系列操作组织在一起。

6. 对象栈上分配 （对象逃逸）
    我们通过JVM内存分配可以知道JAVA中的对象都是在堆上进行分配，当对象没有被引用的时候，需要依靠GC进行回收内
    存，如果对象数量较多的时候，会给GC带来较大压力，也间接影响了应用的性能。为了减少临时对象在堆内分配的数 量，JVM通过 逃逸分析 确定该对象不会被外部访问。如果不会逃逸可以将该对象在 栈上分配 内存，这样该对象所占用的
    内存空间就可以随栈帧出栈而销毁，就减轻了垃圾回收的压力。
    对象逃逸分析 ：就是分析对象动态作用域，当一个对象在方法中被定义后，它可能被外部方法所引用，例如作为调用参数传递到其他地方中

  ### 总结流程图：

  ![e4c734b232d0428e9e6a1f32f63bac1c](E:\图灵课堂\JVM\JVM专题.assets\e4c734b232d0428e9e6a1f32f63bac1c.png)

  

  ### 对象在栈上分配流程

  栈上分配是JVM提供的一项优化技术。

  基本思想如下所示：

  对于那些线程私有的对象（即：不可能被其他线程访问的对象），可以将它们打散分配在栈上，而不是分配在堆上。

  分配在栈上的好处是可以在函数调用结束后自行销毁，而不需要垃圾回收器的介入，从而提高系统的性能。

  对于大量的零散小对象，栈上分配提供了一种很好的对象分配优化策略，栈上分配速度快，并且可以有效避免GC带来的负面影响，但是由于和堆空间相比，栈空间较小，因此对于大对象无法也不适合在栈上分配。 

  **栈上分配的技术基础**

  - **逃逸分析**：逃逸分析的目的是判断对象的作用域**是否有可能逃逸出函数体**。

    对于线程私有的对象，可以分配在栈上，⽽不是分配在堆上。好处是⽅法执⾏完，对象⾃⾏销毁，不需要gc介⼊。可以提⾼性能。

    ⽽栈上分配的⼀个技术基础（如果关闭逃逸分析或关闭标量替换，那么⽆法将对象分配在栈上）就是逃逸分析。

    逃逸分析的⽬的是判断对象的作⽤域是否有可能逃逸出函数体。
    
  - **标量替换**：允许**将对象打散分配在栈上**。比如：若一个对象拥有两个字段，会将这两个字段视作局部变量进行分配。

    条件1> 通过逃逸分析确定该对象不会被外部访问。
    条件2> 对象可以被进一步分解，即聚合量。JVM不会创建该对象，而会将该对象成员变量 **分** **解若干个被这个方法使用的成员变量所代替**。这些代替的成员变量在 **栈帧**或 **寄存器**上分配空间。

    

  具体比较复杂接下来我们详细测试下

  测试验证：

  ```java
  public class AllotOnStack {
   
    public static void main ( String [] args ) {
        long start = System . currentTimeMillis ();
        for ( int i = 0 ; i < 100000000 ; i ++ ) {
        alloc ();
        }
        long end = System . currentTimeMillis ();
    	System . out . println ( end ‐ start );
    }
   
   private static void alloc () {
    User user = new User ();
    user . setId ( 1 );
    user . setName ( "zhuge" ); 26 
   }
  }
  ```


  栈上分配依赖于逃逸分析和标量替换
  对象在Eden区分配
  大多数情况下，对象在新生代中 Eden 区分配。当 Eden 区没有足够空间进行分配时，虚拟机将发起一次Minor GC。我
  们来进行实际测试一下。
  在测试之前我们先来看看 Minor GC和Full GC 有什么不同呢？
  Minor GC/Young GC ：指发生新生代的的垃圾收集动作，Minor GC非常频繁，回收速度一般也比较快。
  Major GC/Full GC ：一般会回收老年代 ，年轻代，方法区的垃圾，Major GC的速度一般会比Minor GC的慢
  10倍以上。
  Eden与Survivor区默认8:1:1
  大量的对象被分配在eden区，eden区满了后会触发minor gc，可能会有99%以上的对象成为垃圾被回收掉，剩余存活
  的对象会被挪到为空的那块survivor区，下一次eden区满了后又会触发minor gc，把eden区和survivor区垃圾对象回
  收，把剩余存活的对象一次性挪动到另外一块为空的survivor区，因为新生代的对象都是朝生夕死的，存活时间很短，所
  以JVM默认的8:1:1的比例是很合适的， 让eden区尽量的大，survivor区够用即可，
  JVM默认有这个参数-XX:+UseAdaptiveSizePolicy(默认开启)，会导致这个8:1:1比例自动变化，如果不想这个比例有变
  化可以设置参数-XX:-UseAdaptiveSizePolicy
  示例：
  1 // 添加运行 JVM 参数： ‐XX:+PrintGCDetails

  

  ```java
  public class GCTest {
    public static void main ( String [] args ) throws InterruptedException {
    byte [] allocation1 , allocation2 /*, allocation3, allocation4, allocation5, allocation6*/ ;
    allocation1 = new byte [ 60000 * 1024 ];
   
    //allocation2 = new byte[8000*1024];
   
    /*allocation3 = new byte[1000*1024];
    allocation4 = new byte [ 1000 * 1024 ];
    allocation5 = new byte [ 1000 * 1024 ];
    allocation6 = new byte [ 1000 * 1024 ]; */
    }
  }
  
  ```

  运行结果：

   Heap
   PSYoungGen total 76288 K , used 65536 K [ 0x000000076b400000 , 0x0000000770900000 , 0x00000007c0000000 )
    eden space 65536 K , 100 % used [ 0x000000076b400000 , 0x000000076f400000 , 0x000000076f400000 )
    from space 10752 K , 0 % used [ 0x000000076fe80000 , 0x000000076fe80000 , 0x0000000770900000 )
    to space 10752 K , 0 % used [ 0x000000076f400000 , 0x000000076f400000 , 0x000000076fe80000 )
    ParOldGen total 175104 K , used 0 K [ 0x00000006c1c00000 , 0x00000006cc700000 , 0x000000076b400000 )
    object space 175104 K , 0 % used [ 0x00000006c1c00000 , 0x00000006c1c00000 , 0x00000006cc700000 )
    Metaspace used 3342 K , capacity 4496 K , committed 4864 K , reserved 1056768 K
   class space used 361 K , capacity 388 K , committed 512 K , reserved 1048576 K
  我们可以看出eden区内存几乎已经被分配完全（即使程序什么也不做，新生代也会使用至少几M内存）。 假如我们再为
  allocation2分配内存会出现什么情况呢？
  1 // 添加运行 JVM 参数： ‐XX:+PrintGCDetails

  ```java
  public class GCTest {
  	public static void main ( String [] args ) throws InterruptedException {
   		byte [] allocation1 , allocation2 /*, allocation3, allocation4, allocation5, allocation6*/ ;
   		allocation1 = new byte [ 60000 * 1024 ];
   		allocation2 = new byte [ 8000 * 1024 ];
  	}
  }
  ```

  运行结果：
  [ GC ( Allocation Failure ) [ PSYoungGen : 65253 K ‐> 936 K ( 76288 K )] 65253 K ‐> 60944 K ( 251392 K ), 0.0279083 secs ] [ Times :
  user = 0.13 sys = 0.02 , real = 0.03 secs ]
   Heap
  PSYoungGen total 76288 K , used 9591 K [ 0x000000076b400000 , 0x0000000774900000 , 0x00000007c0000000 )
  eden space 65536 K , 13 % used [ 0x000000076b400000 , 0x000000076bc73ef8 , 0x000000076f400000 )
   from space 10752 K , 8 % used [ 0x000000076f400000 , 0x000000076f4ea020 , 0x000000076fe80000 )
   to space 10752 K , 0 % used [ 0x0000000773e80000 , 0x0000000773e80000 , 0x0000000774900000 )
  ParOldGen total 175104 K , used 60008 K [ 0x00000006c1c00000 , 0x00000006cc700000 , 0x000000076b400000 )
   object space 175104 K , 34 % used [ 0x00000006c1c00000 , 0x00000006c569a010 , 0x00000006cc700000 )
   Metaspace used 3342 K , capacity 4496 K , committed 4864 K , reserved 1056768 K
   class space used 361 K , capacity 388 K , committed 512 K , reserved 1048576 K
  简单解释一下为什么会出现这种情况： 因为给allocation2分配内存的时候eden区内存几乎已经被分配完了，我们刚刚讲
  了当Eden区没有足够空间进行分配时，虚拟机将发起一次Minor GC，GC期间虚拟机又发现allocation1无法存入
  Survior空间，所以只好把新生代的对象 提前转移到老年代 中去，老年代上的空间足够存放allocation1，所以不会出现
  Full GC。执行Minor GC后，后面分配的对象如果能够存在eden区的话，还是会在eden区分配内存。可以执行如下代码
  验证：

  ```java
   public class GCTest {
   	public static void main ( String [] args ) throws InterruptedException {
   	byte [] allocation1 , allocation2 , allocation3 , allocation4 , allocation5 , allocation6 ;
   	allocation1 = new byte [ 60000 * 1024 ];
   
   	allocation2 = new byte [ 8000 * 1024 ];
   
   	allocation3 = new byte [ 1000 * 1024 ];
   	allocation4 = new byte [ 1000 * 1024 ];
   	 allocation5 = new byte [ 1000 * 1024 ];
   	 allocation6 = new byte [ 1000 * 1024 ];
    }
   }
  ```

  15 运行结果：
   [ GC ( Allocation Failure ) [ PSYoungGen : 65253 K ‐> 952 K ( 76288 K )] 65253 K ‐> 60960 K ( 251392 K ), 0.0311467 secs ] [ Times :
  user = 0.08 sys = 0.02 , real = 0.03 secs ]
    Heap
   PSYoungGen total 76288 K , used 13878 K [ 0x000000076b400000 , 0x0000000774900000 , 0x00000007c0000000 )
   eden space 65536 K , 19 % used [ 0x000000076b400000 , 0x000000076c09fb68 , 0x000000076f400000 )
    from space 10752 K , 8 % used [ 0x000000076f400000 , 0x000000076f4ee030 , 0x000000076fe80000 )
    to space 10752 K , 0 % used [ 0x0000000773e80000 , 0x0000000773e80000 , 0x0000000774900000 )
    ParOldGen total 175104 K , used 60008 K [ 0x00000006c1c00000 , 0x00000006cc700000 , 0x000000076b400000 )
    object space 175104 K , 34 % used [ 0x00000006c1c00000 , 0x00000006c569a010 , 0x00000006cc700000 )
   Metaspace used 3343 K , capacity 4496 K , committed 4864 K , reserved 1056768 K
    class space used 361 K , capacity 388 K , committed 512 K , reserved 1048576 K
  **大对象直接进入老年代**
  大对象就是需要大量连续内存空间的对象（比如：字符串、数组）。JVM参数 -XX:PretenureSizeThreshold 可以设置大
  对象的大小，如果对象超过设置大小会直接进入老年代，不会进入年轻代，这个参数只在 Serial 和ParNew两个收集器下
  有效。 比如设置JVM参数：-XX:PretenureSizeThreshold=1000000 (单位是字节) -XX:+UseSerialGC ，再执行下上面的第一
  个程序会发现大对象直接进了老年代
  为什么要这样呢？
  为了避免为大对象分配内存时的复制操作而降低效率。
  长期存活的对象将进入老年代
  既然虚拟机采用了分代收集的思想来管理内存，那么内存回收时就必须能识别哪些对象应放在新生代，哪些对象应放在
  老年代中。为了做到这一点，虚拟机给每个对象一个对象年龄（Age）计数器。
  如果对象在 Eden 出生并经过第一次 Minor GC 后仍然能够存活，并且能被 Survivor 容纳的话，将被移动到 Survivor
  空间中，并将对象年龄设为1。对象在 Survivor 中每熬过一次 MinorGC，年龄就增加1岁，当它的年龄增加到一定程度
  （默认为15岁，CMS收集器默认6岁，不同的垃圾收集器会略微有点不同），就会被晋升到老年代中。对象晋升到老年代
  的年龄阈值，可以通过参数 -XX:MaxTenuringThreshold 来设置。
  对象动态年龄判断
  当前放对象的Survivor区域里(其中一块区域，放对象的那块s区)，一批对象的总大小大于这块Survivor区域内存大小的
  50%(-XX:TargetSurvivorRatio可以指定)，那么此时 大于等于 这批对象年龄最大值的对象，就可以直接进入老年代了，
  例如Survivor区域里现在有一批对象，年龄1+年龄2+年龄n的多个年龄对象总和超过了Survivor区域的50%，此时就会
  把年龄n(含)以上的对象都放入老年代。这个规则其实是希望那些可能是长期存活的对象，尽早进入老年代。 对象动态年
  龄判断机制一般是在minor gc之后触发的。
  老年代空间分配担保机制
  年轻代每次 minor gc 之前JVM都会计算下老年代 剩余可用空间
  如果这个可用空间小于年轻代里现有的所有对象大小之和( 包括垃圾对象 )
  就会看一个“-XX:-HandlePromotionFailure”(jdk1.8默认就设置了)的参数是否设置了
  如果有这个参数，就会看看老年代的可用内存大小，是否大于之前每一次minor gc后进入老年代的对象的 平均大小 。
  如果上一步结果是小于或者之前说的参数没有设置，那么就会触发一次Full gc，对老年代和年轻代一起回收一次垃圾，
  如果回收完还是没有足够空间存放新的对象就会发生"OOM"
  当然，如果minor gc之后剩余存活的需要挪动到老年代的对象大小还是大于老年代可用空间，那么也会触发full gc，full
  gc完之后如果还是没有空间放minor gc之后的存活对象，则也会发生“OOM” 

  #### 归纳总结：

**一般情况下，新创建的对象都会被分配到Eden区，一些特殊的大的对象会直接分配到Old区。**

```
我是一个普通的Java对象,我出生在Eden区,在Eden区我还看到和我长的很像的小兄弟,我们在Eden区中玩了挺长时间。有一天Eden区中的人实在是太多了,我就被迫去了Survivor区的“From”区,自从去了Survivor区,我就开始漂了,有时候在Survivor的“From”区,有时候在Survivor的“To”区,居无定所。直到我18岁的时候,爸爸说我成人了,该去社会上闯闯了。于是我就去了年老代那边,年老代里,人很多,并且年龄都挺大的。
```

![image-20240116215219872](E:\图灵课堂\JVM\JVM专题.assets\image-20240116215219872.png)



### 什么时候会触发Full   GC？

触发Full GC执行的情况有如下四种。

1. Old区空间不足
Old区只有 在新生代对象转入及创建为大对象、大数组时才会出现不足的现象，当执行Full GC后空间仍然不 足，则抛出如下错误： java.lang.OutOfMemoryError: Java heap space 为避免以上两种状况引起 的FullGC，调优时应尽量做到让对象在Minor GC阶段被回收、让对象在新生代多存活一段时间及不 要创建过大的对象及数组。
2. Permanet Generation空间满
PermanetGeneration(永久代)中存放的为一些class的信息等，当系统中 要加载的类、反射的类和调用的方法较多时，Permanet Generation可能会被占满，在未配置为采 用CMS GC的情况下会执行Full GC。如果经过Full GC仍然回收不了，那么JVM会抛出如下错误信 息： java.lang.OutOfMemoryError: PermGen space 为避免Perm Gen占满造成Full GC现象，可 采用的方法为增大Perm Gen空间或转为使用CMS GC。
3. 将对象放入堆时出现promotion failed和concurrent mode failure
对于采用CMS进行旧生代GC的 程序而言，尤其要注意GC日志中是否有promotion failed和concurrent mode failure两种状况，当 这两种状况出现时可能会触发Full GC。 promotionfailed是在进行Minor GC时，survivor space放 不下、对象只能放入旧生代，而此时旧生代也放不下造成的；concurrent mode failure是在执行 CMS GC的过程中同时有对象要放入旧生代，而此时旧生代空间不足造成的。 应对措施为：增大 survivorspace、旧生代空间或调低触发并发GC的比率，但在JDK 5.0+、6.0+的版本中有可能会由 于JDK的bug29导致CMS在remark完毕后很久才触发sweeping动作。对于这种状况，可通过设置XX:CMSMaxAbortablePrecleanTime=5（单位为ms）来避免。
4. 统计得到的Minor GC晋升到旧生代的平均大小大于旧生代的剩余空间
这是一个较为复杂的触发 情况，Hotspot为了避免由于新生代对象晋升到旧生代导致旧生代空间不足的现象，在进行Minor GC时，做了一个判断，如果之前统计所得到的Minor GC晋升到旧生代的平均大小大于旧生代的剩 余空间，那么就直接触发Full GC。 例如程序第一次触发MinorGC后，有6MB的对象晋升到旧生 代，那么当下一次Minor GC发生时，首先检查旧生代的剩余空间是否大于6MB，如果小于6MB， 则执行Full GC。 当新生代采用PSGC时，方式稍有不同，PS GC是在Minor GC后也会检查，例如上 面的例子中第一次Minor GC后，PS GC会检查此时旧生代的剩余空间是否大于6MB，如小于，则触 发对旧生代的回收。 除了以上4种状况外，对于使用RMI来进行RPC或管理的Sun JDK应用而言，默 认情况下会一小时执行一次Full GC。可通过在启动时通过- javaDsun.rmi.dgc.client.gcInterval=3600000来设置Full GC执行的间隔时间或通过-XX:+ DisableExplicitGC来禁止RMI调用System.gc。 

# 何时触发垃圾回收                  

什么时候触发垃圾回收除了手动触发之外取决于两个因素：内存的压力和垃圾回收算法。

### 体验与验证

#### 2.4.5.1 使用visualvm

**visualgc插件下载链接 ：**[https://visualvm.github.io/pluginscenters.html](https://visualvm.github.io/pluginscenters.html)

**选择对应JDK版本链接--->Tools--->Visual GC**
**若上述链接找不到合适的，大家也可以自己在网上下载对应的版本**

#### 2.4.5.2 堆内存溢出

* **代码**

```java
@RestController
public class HeapController {
    List<Person> list=new ArrayList<Person>();
    @GetMapping("/heap")
    public String heap(){
        while(true){
            list.add(new Person());
        }
    }
}
```

> **记得设置参数比如-Xmx20M -Xms20M**

* **运行结果**

`访问`：[http://localhost:8080/heap](http://localhost:8080/heap)

```
Exception in thread "http-nio-8080-exec-2" java.lang.OutOfMemoryError: GC overhead limit exceeded
```

#### 2.4.5.3 方法区内存溢出

> **比如向方法区中添加Class的信息**

* **asm依赖和Class代码**

```xml
<dependency>
    <groupId>asm</groupId>
    <artifactId>asm</artifactId>
    <version>3.3.1</version>
</dependency>
```

```java
public class MyMetaspace extends ClassLoader {
    public static List<Class<?>> createClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (int i = 0; i < 10000000; ++i) {
            ClassWriter cw = new ClassWriter(0);
            cw.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC, "Class" + i, null,
                    "java/lang/Object", null);
            MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                    "()V", null, null);
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
                    "<init>", "()V");
            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(1, 1);
            mw.visitEnd();
            Metaspace test = new Metaspace();
            byte[] code = cw.toByteArray();
            Class<?> exampleClass = test.defineClass("Class" + i, code, 0, code.length);
            classes.add(exampleClass);
        }
        return classes;
    }
}
```

* **代码**

```java
@RestController
public class NonHeapController {
    List<Class<?>> list=new ArrayList<Class<?>>();

    @GetMapping("/nonheap")
    public String nonheap(){
        while(true){
            list.addAll(MyMetaspace.createClasses());
        }
    }
}
```

> 设置Metaspace的大小，比如-XX:MetaspaceSize=50M -XX:MaxMetaspaceSize=50M

* **运行结果**

**访问->**[http://localhost:8080/nonheap](http://localhost:8080/nonheap)

```
java.lang.OutOfMemoryError: Metaspace
at java.lang.ClassLoader.defineClass1(Native Method) ~[na:1.8.0_191]
at java.lang.ClassLoader.defineClass(ClassLoader.java:763) ~[na:1.8.0_191]
```

#### 2.4.5.4 虚拟机栈

![16502794300283015002ffy](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1/38762a199b4c41ada6766cb40738ed61.png)

* **代码演示StackOverFlow**

```java
public class StackDemo {
    public static long count=0;
    public static void method(long i){
        System.out.println(count++);
        method(i);
    }
    public static void main(String[] args) {
        method(1);
    }
}
```

* **运行结果**

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1650279430028/0e6f6fef869c43ac990bd89249f47b6a.png)

* **说明**

```
Stack Space用来做方法的递归调用时压入Stack Frame(栈帧)。所以当递归调用太深的时候，就有可能耗尽Stack Space，爆出StackOverflow的错误。

-Xss128k：设置每个线程的堆栈大小。JDK 5以后每个线程堆栈大小为1M，以前每个线程堆栈大小为256K。根据应用的线程所需内存大小进行调整。在相同物理内存下，减小这个值能生成更多的线程。但是操作系统对一个进程内的线程数还是有限制的，不能无限生成，经验值在3000~5000左右。

线程栈的大小是个双刃剑，如果设置过小，可能会出现栈溢出，特别是在该线程内有递归、大的循环时出现溢出的可能性更大，如果该值设置过大，就有影响到创建栈的数量，如果是多线程的应用，就会出现内存溢出的错误。
```



#### 2.4.5.5 关联问题

* **如何理解Minor/Major/Full GC**

```
Minor GC:新生代
Major GC:老年代
Full GC:新生代+老年代
```

* **为什么需要Survivor区?只有Eden不行吗？**

```
如果没有Survivor,Eden区每进行一次Minor GC,存活的对象就会被送到老年代。
这样一来，老年代很快被填满,触发Major GC(因为Major GC一般伴随着Minor GC,也可以看做触发了Full GC)。
老年代的内存空间远大于新生代,进行一次Full GC消耗的时间比Minor GC长得多。
执行时间长有什么坏处?频发的Full GC消耗的时间很长,会影响大型程序的执行和响应速度。

可能你会说，那就对老年代的空间进行增加或者较少咯。
假如增加老年代空间，更多存活对象才能填满老年代。虽然降低Full GC频率，但是随着老年代空间加大,一旦发生Full GC,执行所需要的时间更长。
假如减少老年代空间，虽然Full GC所需时间减少，但是老年代很快被存活对象填满,Full GC频率增加。

所以Survivor的存在意义,就是减少被送到老年代的对象,进而减少Full GC的发生,Survivor的预筛选保证,只有经历16次Minor GC还能在新生代中存活的对象,才会被送到老年代。
```

* **为什么需要两个Survivor区？**

```
最大的好处就是解决了碎片化。也就是说为什么一个Survivor区不行?第一部分中,我们知道了必须设置Survivor区。假设现在只有一个Survivor区,我们来模拟一下流程:
刚刚新建的对象在Eden中,一旦Eden满了,触发一次Minor GC,Eden中的存活对象就会被移动到Survivor区。这样继续循环下去,下一次Eden满了的时候,问题来了,此时进行Minor GC,Eden和Survivor各有一些存活对象,如果此时把Eden区的存活对象硬放到Survivor区,很明显这两部分对象所占有的内存是不连续的,也就导致了内存碎片化。
永远有一个Survivor space是空的,另一个非空的Survivor space无碎片。
```

* **新生代中Eden:S1:S2为什么是8:1:1？**

```
新生代中的可用内存：复制算法用来担保的内存为9:1
可用内存中Eden：S1区为8:1
即新生代中Eden:S1:S2 = 8:1:1
现代的商业虚拟机都采用这种收集算法来回收新生代，IBM公司的专门研究表明，新生代中的对象大概98%是“朝生夕死”的
```

* **堆内存中都是线程共享的区域吗？**

```
JVM默认为每个线程在Eden上开辟一个buffer区域，用来加速对象的分配，称之为TLAB，全称:Thread Local Allocation Buffer。
对象优先会在TLAB上分配，但是TLAB空间通常会比较小，如果对象比较大，那么还是在共享区域分配。
```

#### 触发原因总结：

1. 内存的压力：当JVM检测到（Eden区S区Old区方法区）内存不足时，就会触发垃圾回收。具体来说，当JVM中的堆内存占用超过了某个阈值时，垃圾回收就会被触发。这个阈值可以通过JVM参数进行配置。

2. 垃圾回收算法：JVM中有许多不同的垃圾回收算法，比如标记-清除、复制、标记-整理等。每个算法都有不同的触发条件。一般来说，当对象的生命周期结束时，垃圾回收就会被触发。例如，当一个对象没有被任何引用指向时，它就可以被认为是垃圾，并且可以在垃圾回收时被清理掉。
3. 手动触发：System.gc()(通知jvm进行一次垃圾回收，具体执行还要看JVM，另外在代码中尽量不要用，毕竟这里调用的是FullGC一次还是很消耗资源的。

需要注意的是，垃圾回收的触发并不是立即的。JVM通常使用分代垃圾回收的策略，即将堆内存分为新生代和老年代。新生代中的对象生命周期相对较短，因此会经常进行垃圾回收；而老年代中的对象生命周期较长，因此垃圾回收频率较低。具体的触发时机和频率也取决于具体的垃圾回收算法和JVM的实现。

## 常用命令   

> 查看java进程

```
The jps command lists the instrumented Java HotSpot VMs on the target system. The command is limited to reporting information on JVMs for which it has the access permissions.
```

![](E:/图灵课堂/JVM/images/41.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/fcaee39e479c4d078f16ecb325619c53.png)

### jinfo

> （1）实时查看和调整JVM配置参数

```
The jinfo command prints Java configuration information for a specified Java process or core file or a remote debug server. The configuration information includes Java system properties and Java Virtual Machine (JVM) command-line flags.
```

> （2）查看用法
>
> jinfo -flag name PID     查看某个java进程的name属性的值

```
jinfo -flag MaxHeapSize PID 
jinfo -flag UseG1GC PID
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/4be28005e8a244f7b357d277a9244dbe.png)

> （3）修改
>
> **参数只有被标记为manageable的flags可以被实时修改**

```
jinfo -flag [+|-] PID
jinfo -flag <name>=<value> PID
```

> （4）查看曾经赋过值的一些参数

```
jinfo -flags PID
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/01493e1542304c32af3dd5e5dfdcaf39.png)

### jstat

> （1）查看虚拟机性能统计信息

```
The jstat command displays performance statistics for an instrumented Java HotSpot VM. The target JVM is identified by its virtual machine identifier, or vmid option.
```

> （2）查看类装载信息

```
jstat -class PID 1000 10   查看某个java进程的类装载信息，每1000毫秒输出一次，共输出10次
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/f672e595715a452bb7a3abd3301c74d0.png)

> （3）查看垃圾收集信息

```
jstat -gc PID 1000 10
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/06ce7f95993346b283c7f2f86fe1516f.png)

### jstack

> （1）查看线程堆栈信息

```
The jstack command prints Java stack traces of Java threads for a specified Java process, core file, or remote debug server.
```

> （2）用法

```
jstack PID
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/f3a09e00012043baafea183e17291d48.png)

> (4)排查死锁案例

* DeadLockDemo

```java
//运行主类
public class DeadLockDemo
{
    public static void main(String[] args)
    {
        DeadLock d1=new DeadLock(true);
        DeadLock d2=new DeadLock(false);
        Thread t1=new Thread(d1);
        Thread t2=new Thread(d2);
        t1.start();
        t2.start();
    }
}
//定义锁对象
class MyLock{
    public static Object obj1=new Object();
    public static Object obj2=new Object();
}
//死锁代码
class DeadLock implements Runnable{
    private boolean flag;
    DeadLock(boolean flag){
        this.flag=flag;
    }
    public void run() {
        if(flag) {
            while(true) {
                synchronized(MyLock.obj1) {
                    System.out.println(Thread.currentThread().getName()+"----if获得obj1锁");
                    synchronized(MyLock.obj2) {
                        System.out.println(Thread.currentThread().getName()+"----if获得obj2锁");
                    }
                }
            }
        }
        else {
            while(true){
                synchronized(MyLock.obj2) {
                    System.out.println(Thread.currentThread().getName()+"----否则获得obj2锁");
                    synchronized(MyLock.obj1) {
                        System.out.println(Thread.currentThread().getName()+"----否则获得obj1锁");

                    }
                }
            }
        }
    }
}
```

* 运行结果

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/518fbc7a34494f1e91d914f5b8e2de8f.png)

* jstack分析

![](E:/图灵课堂/JVM/images/48.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/b2a12fc243fa43248a12df586ee1df5a.png)

> 把打印信息拉到最后可以发现

![](E:/图灵课堂/JVM/images/49.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/9e2c9900320244d4a8ed33f3e64a117a.png)

### jmap

> （1）生成堆转储快照

```
The jmap command prints shared object memory maps or heap memory details of a specified process, core file, or remote debug server.
```

> （2）打印出堆内存相关信息

```
jmap -heap PID
```

```
jinfo -flag UsePSAdaptiveSurvivorSizePolicy 35352
-XX:SurvivorRatio=8
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/ac32aa9fcc89458eb5cac86209bdfa0a.png)

> （3）dump出堆内存相关信息

```
jmap -dump:format=b,file=heap.hprof PID
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/837bbf874f234b6f9a2f432c31eedbc6.png)

> （4）要是在发生堆内存溢出的时候，能自动dump出该文件就好了

一般在开发中，JVM参数可以加上下面两句，这样内存溢出时，会自动dump出该文件

-XX:+HeapDumpOnOutOfMemoryError     -XX:HeapDumpPath=heap.hprof

```
设置堆内存大小: -Xms20M -Xmx20M
启动，然后访问localhost:9090/heap，使得堆内存溢出
```

## JVM内部的优化逻辑

### JVM的执行引擎

> javac编译器将Person.java源码文件编译成class文件[我们把这里的编译称为前期编译]，交给JVM运行，因为JVM只能认识class字节码文件。同时在不同的操作系统上安装对应版本的JDK，里面包含了各自屏蔽操作系统底层细节的JVM，这样同一份class文件就能运行在不同的操作系统平台之上，得益于JVM。这也是Write Once，Run Anywhere的原因所在。

> 最终JVM需要把字节码指令转换为机器码，可以理解为是0101这样的机器语言，这样才能运行在不同的机器上，那么由字节码转变为机器码是谁来做的呢？说白了就是谁来执行这些字节码指令的呢？这就是执行引擎

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/de2b24e15a3d483987582d8b1c267835.png)

#### 解释执行

Interpreter，解释器逐条把字节码翻译成机器码并执行，跨平台的保证。

刚开始执行引擎只采用了解释执行的，但是后来发现某些方法或者代码块被调用执行的特别频繁时，就会把这些代码认定为“热点代码”。

#### 即时编译器

Just-In-Time compilation(JIT)，即时编译器先将字节码编译成对应平台的可执行文件，运行速度快。即时编译器会把这些热点代码编译成与本地平台关联的机器码，并且进行各层次的优化，保存到内存中。

#### JVM采用哪种方式

JVM采取的是混合模式，也就是解释+编译的方式，对于大部分不常用的代码，不需要浪费时间将其编译成机器码，只需要用到的时候再以解释的方式运行；对于小部分的热点代码，可以采取编译的方式，追求更高的运行效率。

#### 即时编译器类型

（1）HotSpot虚拟机里面内置了两个JIT：C1和C2

> C1也称为Client Compiler，适用于执行时间短或者对启动性能有要求的程序
>
> C2也称为Server Compiler，适用于执行时间长或者对峰值性能有要求的程序

（2）Java7开始，HotSpot会使用分层编译的方式

> 分层编译也就是会结合C1的启动性能优势和C2的峰值性能优势，热点方法会先被C1编译，然后热点方法中的热点会被C2再次编译
>
> -XX:+TieredCompilation开启参数

#### JVM的分层编译5大级别：

**0.解释执行**

**1.简单的C1编译**：仅仅使用我们的C1做一些简单的优化，不会开启Profiling

**2.受限的C1编译代码：**只会执行我们的方法调用次数以及循环的回边次数（多次执行的循环体）Profiling的C1编译

**3.完全C1编译代码：**我们Profiling里面所有的代码。也会被C1执行

**4.C2编译代码：**这个才是优化的级别。

> 级别越高，我们的应用启动越慢，优化下来开销会越高，同样的，我们的峰值性能也会越高

> 通常C2 代码的执行效率要比 C1 代码的高出 30% 以上

#### 分层编译级别：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/21dfd9e5cca74427a1681bac6f7f0eda.png)

Java 虚拟机内置了 profiling。

> profiling 是指在程序执行过程中，收集能够反映程序执行状态的数据。这里所收集的数据我们称之为程序的 profile。

如果方法的字节码数目比较少（如 getter/setter），而且 3 层的 profiling 没有可收集的数据。

那么，Java 虚拟机断定该方法对于 C1 代码和 C2 代码的执行效率相同。

在这种情况下，Java 虚拟机会在 3 层编译之后，直接选择用 1 层的 C1 编译。

由于这是一个终止状态，因此 Java 虚拟机不会继续用 4 层的 C2 编译。

在 C1 忙碌的情况下，Java 虚拟机在解释执行过程中对程序进行 profiling，而后直接由 4 层的 C2 编译。

在 C2 忙碌的情况下，方法会被 2 层的 C1 编译，然后再被 3 层的 C1 编译，以减少方法在 3 层的执行时间。

> Java 8 默认开启了分层编译。-XX:+TieredCompilation开启参数
>
> 不管是开启还是关闭分层编译，原本用来选择即时编译器的参数 **-client** 和 **-server** 都是无效的。当关闭分层编译的情况下，Java 虚拟机将直接采用 C2。
>
> 如果你希望只是用 C1，那么你可以在打开分层编译的情况下使用参数 **-XX:TieredStopAtLevel=1**。在这种情况下，Java 虚拟机会在解释执行之后直接由 1 层的 C1 进行编译。

#### 热点代码：

在运行过程中会被即时编译的“热点代码” 有两类，即：

* **被多次调用的方法**
* **被多次执行的循环体**

对于第一种，编译器会将整个方法作为编译对象，这也是标准的JIT 编译方式。对于第二种是由循环体出发的，但是编译器依然会以整个方法（而不是单独的循环体）作为编译对象，因为发生在方法执行过程中，称为栈上替换（On Stack Replacement，简称为 OSR 编译，即方法栈帧还在栈上，方法就被替换了）。

#### 如何找到热点代码？

判断一段代码是否是热点代码，是不是需要触发即时编译，这样的行为称为热点探测（Hot Spot Detection），探测算法有两种，分别如下：

* **基于采样的热点探测（Sample Based Hot Spot Detection）：**虚拟机会周期的对各个线程栈顶进行检查，如果某些方法经常出现在栈顶，这个方法就是“热点方法”。好处是实现简单、高效，很容易获取方法调用关系。缺点是很难确认方法的 reduce，容易受到线程阻塞或其他外因扰乱。
* **基于计数器的热点探测（Counter Based Hot Spot Detection）**：为每个方法（甚至是代码块）建立计数器，执行次数超过阈值就认为是“热点方法”。优点是统计结果精确严谨。缺点是实现麻烦，不能直接获取方法的调用关系。

HotSpot 使用的是第二种——基于计数器的热点探测，并且有两类计数器：方法调用计数器（Invocation Counter ）和回边计数器（Back Edge Counter ）。

这两个计数器都有一个确定的阈值，超过后便会触发 JIT 编译。

### java两大计数器：

(1)**首先是方法调用计数器** 。Client 模式下默认阈值是 **1500** 次，在 Server 模式下是 **10000**次，这个阈值可以通过 **-XX：CompileThreadhold** 来人为设定。如果不做任何设置，方法调用计数器统计的并不是方法被调用的绝对次数，而是一个相对的执行频率，即一段时间之内的方法被调用的次数。当超过一定的时间限度，如果方法的调用次数仍然不足以让它提交给即时编译器编译，那么这个方法的调用计数器就会被减少一半，这个过程称为方法调用计数器热度的衰减（Counter Decay），而这段时间就成为此方法的统计的半衰周期（ Counter Half Life Time）。进行热度衰减的动作是在虚拟机进行垃圾收集时顺便进行的，可以使用虚拟机参数 **-XX：CounterHalfLifeTime** 参数设置半衰周期的时间，单位是秒。整个 JIT 编译的交互过程如下图。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/5e293e5b0b1649a1aaf70621b097d78d.png)

**（2）第二个回边计数器** ，作用是统计一个方法中循环体代码执行的次数，在字节码中遇到控制流向后跳转的指令称为“回边”（ Back Edge ）。显然，建立回边计数器统计的目的就是为了触发 OSR 编译。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/d4c010ec25894932a5ff0d4f9fb72641.png)

关于这个计数器的阈值， HotSpot 提供了 **-XX：BackEdgeThreshold** 供用户设置，但是当前的虚拟机实际上使用了 **-XX：OnStackReplacePercentage** 来简介调整阈值，计算公式如下：

* 在 **Client** 模式下， 公式为 方法调用计数器阈值（CompileThreshold）X **OSR 比率**（OnStackReplacePercentage）/ **100** 。其中 OSR 比率默认为 **933**，那么，回边计数器的阈值为 **13995**。
* 在 **Server** 模式下，公式为 方法调用计数器阈值（Compile Threashold）X （**OSR 比率**(OnStackReplacePercentage) - 解释器监控比率（InterpreterProfilePercent））/**100**。
  其中 onStackReplacePercentage 默认值为 **140**，InterpreterProfilePercentage 默认值为 **33**，如果都取默认值，那么 Server 模式虚拟机回边计数器阈值为 **10700** 。

与方法计数器不同，回边计数器没有计数热度衰减的过程，因此这个计数器统计的就是该方法循环执行的绝对次数。当计数器溢出的时候，它还会把方法计数器的值也调整到溢出状态，这样下次再进入该方法的时候就会执行标准编译过程。

可以看到，决定一个方法是否为热点代码的因素有两个：方法的调用次数、循环回边的执行次数。即时编译便是根据这两个计数器的和来触发的。为什么 Java 虚拟机需要维护两个不同的计数器呢？

#### **OSR 编译（不重要，别纠结）**

实际上，除了以方法为单位的即时编译之外，Java 虚拟机还存在着另一种以循环为单位的即时编译，叫做 On-Stack-Replacement（OSR）编译。循环回边计数器便是用来触发这种类型的编译的。

OSR 实际上是一种技术，它指的是在程序执行过程中，动态地替换掉 Java 方法栈桢，从而使得程序能够在非方法入口处进行解释执行和编译后的代码之间的切换。也就是说，我只要遇到回边指令，我就可以触发执行切换。

在不启用分层编译的情况下，触发 OSR 编译的阈值是由参数 -XX:CompileThreshold 指定的阈值的倍数。

该倍数的计算方法为：

**(**OnStackReplacePercentage **-** InterpreterProfilePercentage**)**/**100**

其中 -XX:InterpreterProfilePercentage 的默认值为 33，当使用 C1 时 -XX:OnStackReplacePercentage 为 933，当使用 C2 时为 140。

也就是说，默认情况下，C1 的 OSR 编译的阈值为 13500，而 C2 的为 10700。

在启用分层编译的情况下，触发 OSR 编译的阈值则是由参数 -XX:TierXBackEdgeThreshold 指定的阈值乘以系数。

OSR 编译在正常的应用程序中并不多见。它只在基准测试时比较常见，因此并不需要过多了解。

那么这些即时编译器编译后的代码放哪呢？

#### **Code Cache**

JVM生成的native code存放的内存空间称之为Code Cache；JIT编译、JNI等都会编译代码到native code，其中JIT生成的native code占用了Code Cache的绝大部分空间，他是属于非堆内存的。

简而言之，JVM Code Cache （代码缓存）是JVM存储编译成本机代码的字节码的区域。我们将可执行本机代码的每个块称为 `nmethod` 。 `nmethod` 可能是一个完整的或内联的Java方法。

即时（ **JIT** ）编译器是代码缓存区的最大消费者。这就是为什么一些开发人员将此内存称为JIT代码缓存。

#### Code Cache的优化

代码缓存的大小是固定的。一旦它满了，JVM就不会编译任何额外的代码，因为JIT编译器现在处于关闭状态。此外，我们将收到“ `CodeCache is full… The compiler has been disabled` ”警告消息。因此，我们的应用程序的性能最终会下降。为了避免这种情况，我们可以使用以下大小选项调整代码缓存：

* **InitialCodeCacheSize** –初始代码缓存大小，默认为160K
* **ReservedCodeCacheSize** –默认最大大小为48MB
* **CodeCacheExpansionSize** –代码缓存的扩展大小，32KB或64KB

增加ReservedCodeCacheSize可能是一个解决方案，但这通常只是一个临时解决办法。

幸运的是，JVM提供了一个 **UseCodeCache** 刷新选项来控制代码缓存区域的刷新。其默认值为false。当我们启用它时，它会在满足以下条件时释放占用的区域：

* 代码缓存已满；如果该区域的大小超过某个阈值，则会刷新该区域
* 自上次清理以来已过了特定的时间间隔
* 预编译代码不够热。对于每个编译的方法，JVM都会跟踪一个特殊的热度计数器。如果此计数器的值小于计算的阈值，JVM将释放这段预编译代码

#### Code Cache的查看

为了监控Code Cache（代码缓存）的使用情况，我们需要跟踪当前正在使用的内存的大小。

要获取有关代码缓存使用情况的信息，我们可以指定 `–XX:+PrintCodeCache` JVM选项。运行应用程序后，我们将看到类似的输出：

或者直接设置 `-XX:ReservedCodeCacheSize=3000k`，然后重启

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/535cee33a5274e918f2600eb6d30d5c7.png)

让我们看看这些值的含义：

* 输出中的大小显示内存的最大大小，与 **ReservedCodeCacheSize** 相同
* `used` 是当前正在使用的内存的实际大小
* `max_used` 是已使用的最大尺寸
* `free` 是尚未占用的剩余内存

#### JDK9中的分段代码缓存：

从Java9开始，JVM将代码缓存分为三个不同的段，每个段都包含特定类型的编译代码。更具体地说，有三个部分：

```
-XX:nonNMethoddeHeapSize
-XX:ProfiledCodeHeapSize
-XX:nonprofiedCodeHeapSize
```

这种新结构以不同的方式处理各种类型的编译代码，从而提高了整体性能。

例如，将短命编译代码与长寿命代码分离可以提高方法清理器的性能——主要是因为它需要扫描更小的内存区域。

## AOT和Graal VM

在Java9中，引入了AOT(Ahead-Of-Time)编译器

即时编译器是在程序运行过程中，将字节码翻译成机器码。而AOT是在程序运行之前，将字节码转换为机器码

`优势`：这样不需要在运行过程中消耗计算机资源来进行即时编译

`劣势`：AOT 编译无法得知程序运行时的信息，因此也无法进行基于类层次分析的完全虚方法内联，或者基于程序 profile 的投机性优化（并非硬性限制，我们可以通过限制运行范围，或者利用上一次运行的程序 profile 来绕开这两个限制）

#### Graal VM

> `官网`： https://www.oracle.com/tools/graalvm-enterprise-edition.html
>
> **GraalVM core features include:**
>
> - GraalVM Native Image, available as an early access feature –– allows scripted applications to be compiled ahead of time into a native machine-code binary
> - GraalVM Compiler –– generates compiled code to run applications on a JVM, standalone, or embedded in another system
> - Polyglot Capabilities –– supports Java, Scala, Kotlin, JavaScript, and Node.js
> - Language Implementation Framework –– enables implementing any language for the GraalVM environment
> - LLVM Runtime–– permits native code to run in a managed environment in GraalVM Enterprise

在Java10中，新的JIT编译器Graal被引入

它是一个以Java为主要编程语言，面向字节码的编译器。跟C++实现的C1和C2相比，模块化更加明显，也更加容易维护。

Graal既可以作为动态编译器，在运行时编译热点方法；也可以作为静态编译器，实现AOT编译。

除此之外，它还移除了编程语言之间的边界，并且支持通过即时编译技术，将混杂了不同的编程语言的代码编译到同一段二进制码之中，从而实现不同语言之间的无缝切换。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/3ada9a21301647efb36a4a1424cc020e.png)

## 重新认知JVM

> 之前我们画过一张图，是从Class文件到类装载器，再到运行时数据区的过程。
>
> 现在咱们把这张图不妨丰富完善一下，展示了JVM的大体物理结构图。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/de419cfda85f46de8511ba446793bf97.png)

> JVM Architecture： https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1655274390025/25a7e64156e346629ec14ceebf115a32.png)



## 对象的生命周期

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1650279430028/01c7a2f4dd3a4593bca61e1c89fc5d4b.png)

**创建阶段**

（1）为对象分配存储空间

（2）开始构造对象

（3）从超类到子类对static成员进行初始化

（4）超类成员变量按顺序初始化，递归调用超类的构造方法

（5）子类成员变量按顺序初始化，子类构造方法调用，并且一旦对象被创建，并被分派给某些变量赋值，这个对象的状态就切换到了应用阶段

**应用阶段**

（1）系统至少维护着对象的一个强引用（Strong Reference）

（2）所有对该对象的引用全部是强引用（除非我们显式地使用了：软引用（Soft Reference）、弱引用（Weak Reference）或虚引用（Phantom Reference））

> 引用的定义：
>
> 1.我们的数据类型必须是引用类型
>
> 2.我们这个类型的数据所存储的数据必须是另外一块内存的起始地址

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1650279430028/3900ed8910404854b5595e3c9979bb80.png)

> 引用：
>
> 1.**强引用**
>
> JVM内存管理器从根引用集合（Root Set）出发遍寻堆中所有到达对象的路径。当到达某对象的任意路径都不含有引用对象时，对这个对象的引用就被称为强引用
>
> 2.软引用
>
> 软引用是用来描述一些还有用但是非必须的对象。对于软引用关联的对象，在系统将于发生内存溢出异常之前，将会把这些对象列进回收范围中进行二次回收。
>
> （当你去处理占用内存较大的对象  并且生命周期比较长的，不是频繁使用的）
>
> 问题：软引用可能会降低应用的运行效率与性能。比如：软引用指向的对象如果初始化很耗时，或者这个对象在进行使用的时候被第三方施加了我们未知的操作。
>
> 3.弱引用
>
> 弱引用（Weak Reference）对象与软引用对象的最大不同就在于：GC在进行回收时，需要通过算法检查是否回收软引用对象，而对于Weak引用对象， GC总是进行回收。因此Weak引用对象会更容易、更快被GC回收
>
> 4.虚引用
>
> 也叫幽灵引用和幻影引用，为一个对象设置虚引用关联的唯一目的就是能在这个对象被回收时收到一**个系统通知。也就是说,如果一个对象被设置上了一个虚引用,实际上跟没有设置引用没有**任何的区别

软引用代码Demo：

```java
public class SoftReferenceDemo {
    public static void main(String[] args) {
        //。。。一堆业务代码

        Worker a = new Worker();
//。。业务代码使用到了我们的Worker实例

        // 使用完了a，将它设置为soft 引用类型，并且释放强引用；
        SoftReference sr = new SoftReference(a);
        a = null;
//这个时候他是有可能执行一次GC的
        System.gc();

        // 下次使用时
        if (sr != null) {
            a = (Worker) sr.get();
            System.out.println(a );
        } else {
            // GC由于内存资源不足，可能系统已回收了a的软引用，
            // 因此需要重新装载。
            a = new Worker();
            sr = new SoftReference(a);
        }
    }


}
```

弱引用代码Demo：

```java
public class WeakReferenceDemo {
    public static void main(String[] args) throws InterruptedException {
        //100M的缓存数据
        byte[] cacheData = new byte[100 * 1024 * 1024];
        //将缓存数据用软引用持有
        WeakReference<byte[]> cacheRef = new WeakReference<>(cacheData);
        System.out.println("第一次GC前" + cacheData);
        System.out.println("第一次GC前" + cacheRef.get());
        //进行一次GC后查看对象的回收情况
        System.gc();
        //因为我们不确定我们的System什么时候GC
        Thread.sleep(1000);
        System.out.println("第一次GC后" + cacheData);
        System.out.println("第一次GC后" + cacheRef.get());

        //将缓存数据的强引用去除
        cacheData = null;
        System.gc();    //默认通知一次Full  GC
        //等待GC
        Thread.sleep(500);
        System.out.println("第二次GC后" + cacheData);
        System.out.println("第二次GC后" + cacheRef.get());

//        // 弱引用Map
//        WeakHashMap<String, String> whm = new WeakHashMap<String,String>();
    }
}

```

虚引用代码Demo：

```java
public class PhantomReferenceDemo {
    public static void main(String[] args) throws InterruptedException {
        Object value = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        Thread thread = new Thread(() -> {
            try {
                int cnt = 0;
                WeakReference<byte[]> k;
                while ((k = (WeakReference) referenceQueue.remove()) != null) {
                    System.out.println((cnt++) + "回收了:" + k);
                }
            } catch (InterruptedException e) {
                //结束循环
            }
        });
        thread.setDaemon(true);
        thread.start();


        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            byte[] bytes = new byte[1024 * 1024];
            WeakReference<byte[]> weakReference = new WeakReference<byte[]>(bytes, referenceQueue);
            map.put(weakReference, value);
        }
        System.out.println("map.size->" + map.size());


    }
}
```

finalize方法代码Demo：

```java
public class Finalize {

    private static Finalize save_hook = null;//类变量

    public void isAlive() {
        System.out.println("我还活着");
    }

    @Override
    public void finalize() {
        System.out.println("finalize方法被执行");
        Finalize.save_hook = this;
    }

    public static void main(String[] args) throws InterruptedException {



        save_hook = new Finalize();//对象
        //对象第一次拯救自己
        save_hook = null;
        System.gc();
        //暂停0.5秒等待他
        Thread.sleep(500);
        if (save_hook != null) {
            save_hook.isAlive();
        } else {
            System.out.println("好了，现在我死了");
        }

        //对象第二次拯救自己
        save_hook = null;
        System.gc();
        //暂停0.5秒等待他
        Thread.sleep(500);
        if (save_hook != null) {
            save_hook.isAlive();
        } else {
            System.out.println("我终于死亡了");
        }
    }
}
```

**不可见阶段**

不可见阶段的对象在虚拟机的对象根引用集合中再也找不到直接或者间接的强引用，最常见的就是线程或者函数中的临时变量。程序不在持有对象的强引用。  （但是某些类的静态变量或者JNI是有可能持有的 ）

**不可达阶段**

指对象不再被任何强引用持有，GC发现该对象已经不可达。

### 如何确定一个对象是垃圾？

> **要想进行垃圾回收，得先知道什么样的对象是垃圾。**

#### 2.5.1.1 引用计数法

**对于某个对象而言，只要应用程序中持有该对象的引用，就说明该对象不是垃圾，如果一个对象没有任何指针对其引用，它就是垃圾。**



![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1650279430028/6b6ca2b7d4134992ab0bb380f7e567f8.png)

优缺点 

优点 ：

1、实时性较高，无需等到内存不够的时候，才开始回收，运行时根据对象的计数器是否为0，就可以直接回收。

2、在垃圾回收过程中，应用无需挂起。如果申请内存时，内存不足，则立刻报outofmember错误。

3、区域性，更新对象的计数器时，只是影响到该对象，不会扫描全部对象。

缺点 ：

1、每次对象呗引用时，都需要去更新计数器，有一点时间开销。

2、浪费CPU资源，即使内存够用，仍然在运行时进行计数器的统计。

3、无法解决循环引用问题。（最大的缺点）

#### 2.5.1.2 可达性分析

**通过GC Root的对象，开始向下寻找，看某个对象是否可达                **

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1650279430028/a635e6f9dc41492ab4bca32183fbf3ec.png)

> **能作为GC Root:类加载器、Thread、虚拟机栈的本地变量表、static成员、常量引用、本地方法栈的变量等。GC Roots本质上一组活跃的引用**

```
虚拟机栈（栈帧中的本地变量表）中引用的对象。
方法区中类静态属性引用的对象。
方法区中常量引用的对象。
本地方法栈中JNI（即一般说的Native方法）引用的对象。
```

**收集阶段（Collected）**

GC发现对象处于不可达阶段并且GC已经对该对象的内存空间重新分配做好准备，对象进程收集阶段。如果，该对象的finalize()函数被重写，则执行该函数。

> 1.会影响到JVM的对象以及分配回收速度
>
> 2.可能造成对象再次复活（诈尸）

**终结阶段（Finalized）**

对象的finalize()函数执行完成后，对象仍处于不可达状态，该对象进程终结阶段。

**对象内存空间重新分配阶段（Deallocaled）**

GC对该对象占用的内存空间进行回收或者再分配，该对象彻底消失。

#### 归纳总结：

1，对象没有引用

2，作用域发生未捕获异常

3，程序在作用域正常执行完毕

4，程序执行了System.exit

5，程序发生意外终止（被杀进程等）

#### 标记垃圾算法：

1、  标记-清除算法。

2、  复制算法。

3、标记-整理算法。（本质上后面两种都是在前两种基础上优化组合）

5、分代算法

**标记-清除算法：**分为标记节点（根可达）和清理阶段

采用从根集合进行扫描，对存活的对象进行标记，标记完毕后，再扫描整个空间中未被标记的对象，进行回收，如下图所示。

标记-清除算法不需要进行对象的移动，并且仅对不存活的对象进行处理，在存活对象比较多的情况下极为高效，但由于标记-清除算法直接回收不存活的对象，因此会造成内存碎片（内存中出现很多细小可用内存单元）！正如：CMS是并发运行的，所以在CMS运行的过程中，应用程序仍然在修改堆内存，这就可能会出现一种情况：在CMS的重新标记阶段之后，用户从购物车中删除了一件商品，那么这个商品对象就成为了垃圾。但是因为并发标记阶段已经结束，CMS并不知道这个新的垃圾对象，所以在这次垃圾收集过程中，这个对象并不会被清除，这样的垃圾被称为“浮动垃圾”。当然浮动垃圾在这次垃圾收集中无法被清除，但是在下一次垃圾收集过程中，它们还是会被清除的，所以这个问题不会持续存在。

优点：实现简单仅二个阶段  缺点：碎片化和浮动垃圾

![image-20240117175314041](E:\图灵课堂\JVM\JVM专题.assets\image-20240117175314041.png)

**复制算法：**两块空间每次分配用其中一块，GC回收垃圾后将存活对象复制到另一块空间。

采用从根集合扫描，并将存活对象复制到一块新的，没有使用过的空间中。这种算法当控件存活的对象比较少时，极为高效，但是带来的成本是需要一块内存交换空间用于进行对象的移动。也就是我们前面提到Eden区和s0 s1（survivor0  survivor1）。

优点：吞吐量高，只需一次遍历存活对象（标记整理需两次），不会发生碎片化内存

缺点：内存使用率低，每次都会空闲一块内存不能用来创建对象

![image-20240117175321644](E:\图灵课堂\JVM\JVM专题.assets\image-20240117175321644.png)

**标记-整理算法**：分为标记阶段 和 整理阶段

采用标记-清除算法一样的方式进行对象的标记，但在清除时不同，在回收不存活的对象占用的空间后，会将所有的存活对象往左端空闲空间移动，并更新对应的指针。标记-整理算法是在标记-清除算法的基础上，又进行了对象的移动，因此成本更高，但是却解决了内存碎片的问题。

优点：内存使用率高，不会有碎片化内存

缺点：整理阶段效率不高(有Lisp2，Two-Finger,表格算法，ImmixGC等高效整理算法优化整理)

![image-20240117175330322](E:\图灵课堂\JVM\JVM专题.assets\image-20240117175330322.png)

**分代算法**：前面三种算法结合使用

前面介绍了很多种回收算法，每一种算法都有自己的优点也有缺点，谁都不能替代谁，所以根据垃圾回收对象的特点进行选择，才是明智的选择。
分代算法其实就是这样的，根据回收对象的特点进行选择，在jvm中，年轻代适合使用复制算法，老年代适合使用标记清除或标记压缩算法。

具体使用那种算法取决要垃圾回收器的实现

![image-20240117175335222](E:\图灵课堂\JVM\JVM专题.assets\image-20240117175335222.png)

# 垃圾收集器浅析  

## JVM参数

### 3.1.1 标准参数

```
-version
-help
-server
-cp
```

![](E:/图灵课堂/JVM/images/37.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/ed8dfa7f28724a14914e3d2d35ff4d76.png)

### 3.1.2 -X参数

> 非标准参数，也就是在JDK各个版本中可能会变动

```
-Xint     解释执行
-Xcomp    第一次使用就编译成本地代码
-Xmixed   混合模式，JVM自己来决定
```

![](E:/图灵课堂/JVM/images/38.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/232ab7a37a844eac97a8e306adb93832.png)

### 3.1.3 -XX参数

> 使用得最多的参数类型
>
> 非标准化参数，相对不稳定，主要用于JVM调优和Debug

```
a.Boolean类型
格式：-XX:[+-]<name>            +或-表示启用或者禁用name属性
比如：-XX:+UseConcMarkSweepGC   表示启用CMS类型的垃圾回收器
	 -XX:+UseG1GC              表示启用G1类型的垃圾回收器
b.非Boolean类型
格式：-XX<name>=<value>表示name属性的值是value
比如：-XX:MaxGCPauseMillis=500
```

### 3.1.4 其他参数

```
-Xms1000M等价于-XX:InitialHeapSize=1000M
-Xmx1000M等价于-XX:MaxHeapSize=1000M
-Xss100等价于-XX:ThreadStackSize=100
```

> 所以这块也相当于是-XX类型的参数

### 3.1.5 查看参数

> java -XX:+PrintFlagsFinal -version > flags.txt

![](E:/图灵课堂/JVM/images/39.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/877b4b6e71d2472e8d3893fa3c49bbef.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/729bb6e99209445ca63a4ca605a6a0d4.png)

> 值得注意的是"="表示默认值，":="表示被用户或JVM修改后的值
> 要想查看某个进程具体参数的值，可以使用jinfo，这块后面聊
> 一般要设置参数，可以先查看一下当前参数是什么，然后进行修改

### 3.1.6 设置参数的常见方式

* 开发工具中设置比如IDEA，eclipse
* 运行jar包的时候:java  -XX:+UseG1GC xxx.jar
* web容器比如tomcat，可以在脚本中的进行设置
* 通过jinfo实时调整某个java进程的参数(参数只有被标记为manageable的flags可以被实时修改)

### 3.1.7 实践和单位换算

```
1Byte(字节)=8bit(位)
1KB=1024Byte(字节)
1MB=1024KB
1GB=1024MB
1TB=1024GB
```

```
(1)设置堆内存大小和参数打印
-Xmx100M -Xms100M -XX:+PrintFlagsFinal
(2)查询+PrintFlagsFinal的值
:=true
(3)查询堆内存大小MaxHeapSize
:= 104857600
(4)换算
104857600(Byte)/1024=102400(KB)
102400(KB)/1024=100(MB)
(5)结论
104857600是字节单位
```

### 3.1.8 常用参数含义

| 参数                                                         |                             含义                             |                             说明                             |
| :----------------------------------------------------------- | :----------------------------------------------------------: | :----------------------------------------------------------: |
| -XX:CICompilerCount=3                                        |                        最大并行编译数                        | 如果设置大于1，虽然编译速度会提高，但是同样影响系统稳定性，会增加JVM崩溃的可能 |
| -XX:InitialHeapSize=100M                                     |                         初始化堆大小                         |                         简写-Xms100M                         |
| -XX:MaxHeapSize=100M                                         |                          最大堆大小                          |                         简写-Xms100M                         |
| -XX:NewSize=20M                                              |                       设置年轻代的大小                       |                                                              |
| -XX:MaxNewSize=50M                                           |                        年轻代最大大小                        |                                                              |
| -XX:OldSize=50M                                              |                        设置老年代大小                        |                                                              |
| -XX:MetaspaceSize=50M                                        |                        设置方法区大小                        |                                                              |
| -XX:MaxMetaspaceSize=50M                                     |                        方法区最大大小                        |                                                              |
| -XX:+UseParallelGC                                           |                      使用UseParallelGC                       |                      新生代，吞吐量优先                      |
| -XX:+UseParallelOldGC                                        |                     使用UseParallelOldGC                     |                      老年代，吞吐量优先                      |
| -XX:+UseConcMarkSweepGC                                      |                           使用CMS                            |                     老年代，停顿时间优先                     |
| -XX:+UseG1GC                                                 |                           使用G1GC                           |                 新生代，老年代，停顿时间优先                 |
| -XX:NewRatio                                                 |                        新老生代的比值                        | 比如-XX:Ratio=4，则表示新生代:老年代=1:4，也就是新生代占整个堆内存的1/5 |
| -XX:SurvivorRatio                                            |                    两个S区和Eden区的比值                     | 比如-XX:SurvivorRatio=8，也就是(S0+S1):Eden=2:8，也就是一个S占整个新生代的1/10 |
| -XX:+HeapDumpOnOutOfMemoryError                              |                      启动堆内存溢出打印                      |      当JVM堆内存发生溢出时，也就是OOM，自动生成dump文件      |
| -XX:HeapDumpPath=heap.hprof                                  |                    指定堆内存溢出打印目录                    |             表示在当前目录生成一个heap.hprof文件             |
| -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:g1-gc.log |                         打印出GC日志                         |           可以使用不同的垃圾收集器，对比查看GC情况           |
| -Xss128k                                                     |                    设置每个线程的堆栈大小                    |                    经验值是3000-5000最佳                     |
| -XX:MaxTenuringThreshold=6                                   |                    提升年老代的最大临界值                    |                         默认值为 15                          |
| -XX:InitiatingHeapOccupancyPercent                           |                启动并发GC周期时堆内存使用占比                | G1之类的垃圾收集器用它来触发并发GC周期,基于整个堆的使用率,而不只是某一代内存的使用比. 值为 0 则表示”一直执行GC循环”. 默认值为 45. |
| -XX:G1HeapWastePercent                                       |                    允许的浪费堆空间的占比                    | 默认是10%，如果并发标记可回收的空间小于10%,则不会触发MixedGC。 |
| -XX:MaxGCPauseMillis=200ms                                   |                        G1最大停顿时间                        | 暂停时间不能太小，太小的话就会导致出现G1跟不上垃圾产生的速度。最终退化成Full GC。所以对这个参数的调优是一个持续的过程，逐步调整到最佳状态。 |
| -XX:ConcGCThreads=n                                          |                 并发垃圾收集器使用的线程数量                 |               默认值随JVM运行的平台不同而不同                |
| -XX:G1MixedGCLiveThresholdPercent=65                         |        混合垃圾回收周期中要包括的旧区域设置占用率阈值        |                       默认占用率为 65%                       |
| -XX:G1MixedGCCountTarget=8                                   | 设置标记周期完成后，对存活数据上限为 G1MixedGCLIveThresholdPercent 的旧区域执行混合垃圾回收的目标次数 | 默认8次混合垃圾回收，混合回收的目标是要控制在此目标次数以内  |
| -XX:G1OldCSetRegionThresholdPercent=1                        |           描述Mixed GC时，Old Region被加入到CSet中           |        默认情况下，G1只把10%的Old Region加入到CSet中         |
|                                                              |                                                              |                                                              |

### 垃圾收集器

> 如果说收集算法是内存回收的方法论，那么垃圾收集器就是内存回收的具体实现。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/92eab002f61f4bec9370db573615a2ab.png)

#### 2.5.5.1 Serial

Serial收集器是最基本、发展历史最悠久的收集器，曾经（在JDK1.3.1之前）是虚拟机新生代收集的唯一选择。

它是一种单线程收集器采用**复制算法**，不仅仅意味着它只会使用一个CPU或者一条收集线程去完成垃圾收集工作，更重要的是其在进行垃圾收集的时候需要暂停其他线程。

```
优点：简单高效，拥有很高的单线程收集效率
缺点：收集过程需要暂停所有线程
算法：复制算法
适用范围：新生代
应用：Client模式下的默认新生代收集器
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/31089fda8e1b4a089f8b2495be51ae5f.png)

#### 2.5.5.2 Serial Old

Serial Old收集器是Serial收集器的老年代版本，也是一个单线程收集器，不同的是采用"**标记-整理算法**"，运行过程和Serial收集器一样。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/b48a95033807408b9eda93c48ec59c37.png)

前两种组合优点在于单cpu吞吐量表现优秀，在多CPU下吞吐量不如其他垃圾回收器，堆内存偏大会导致用户线程长时间等待。

-XX:+UseSerialGC 开启此组合垃圾回收器

#### 2.5.5.3 ParNew

可以把这个收集器理解为Serial收集器的多线程版本(多线程进行垃圾回收)。年轻代采用复制算法标记垃圾。关注系统吞吐量，且具备自动调整堆内存大小的特点。同样有Stop The World的问题。

优点在于多CPU停顿时间较短，吞吐量和停顿时间不如G1所以JDK9以后不建议使用。JDK8及之前版本配合CMS老年代垃圾回收器使用

```
优点：在多CPU时，比Serial效率高。
缺点：收集过程暂停所有应用程序线程，单CPU时比Serial效率差。
算法：复制算法
适用范围：新生代
应用：运行在Server模式下的虚拟机中首选的新生代收集器
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/e4add9cd85724a43819c85ec7249433a.png)

#### 2.5.5.4 Parallel Scavenge

Parallel Scavenge收集器是一个新生代收集器，它也是使用**复制算法**的收集器，又是并行的多线程收集器，看上去和ParNew一样，但是Parallel Scanvenge更关注系统的**吞吐量**。

> 吞吐量=运行用户代码的时间/(运行用户代码的时间+垃圾收集时间)
>
> 比如虚拟机总共运行了100分钟，垃圾收集时间用了1分钟，吞吐量=(100-1)/100=99%。
>
> 若吞吐量越大，意味着垃圾收集的时间越短，则用户代码可以充分利用CPU资源，尽快完成程序的运算任务。

```
-XX:MaxGCPauseMillis控制最大的垃圾收集停顿时间，
-XX:GCRatio直接设置吞吐量的大小。
```

#### 2.5.5.5 Parallel Old

Parallel Old收集器是Parallel Scavenge收集器的老年代版本，使用多线程和**标记-整理算法**进行垃圾回收，也是更加关注系统的**吞吐量**。

早期没有ParallelOld之前，吞吐量优先的收集器老生代只能使用串行回收收集器，大大的拖累了吞吐量优先的性能，自从JDK1.6之后，才能真正做到较高效率的吞吐量优先。

Parallel 组合**优点**在于吞吐量高且手动可控。**缺点**在于不能确保单次的停顿时间。**适用**后台任务，不需要用户交互的后台任务且容易产生大量对象（大数据处理，文件导出）

#### 2.5.4.6 CMS

> `官网`： https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/cms.html#concurrent_mark_sweep_cms_collector
>
> CMS(Concurrent Mark Sweep)收集器是一种以获取 `最短回收停顿时间`为目标的收集器。关注系统**暂停时间**
>
> 采用的是"**标记-清除算法**",整个过程分为4步

```
(1)初始标记 CMS initial mark     标记GC Roots直接关联对象，不用Tracing，速度很快
(2)并发标记 CMS concurrent mark  进行GC Roots Tracing
(3)重新标记 CMS remark           修改并发标记因用户程序变动的内容
(4)并发清除 CMS concurrent sweep 清除不可达对象回收空间，同时有新垃圾产生，留着下次清理称为浮动垃圾


```



> 由于整个过程中，并发标记和并发清除，收集器线程可以与用户线程一起工作，所以总体上来说，CMS收集器的内存回收过程是与用户线程一起并发地执行的。只有初始标记需要用户等待。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/568222dbfa6a41d7b29cfa184c6cb103.png)

```
优点：并发收集、低停顿
缺点：产生大量空间碎片和浮动垃圾(并发标记和清理过程部分对象已没被引用了)、并发阶段会降低吞吐量、退化问题(old区内存不足会退化成SerialOld单线程回收老年代)
适用：大型互联网系统中用户请求数据量大，频率高的场景。如订单，商品接口
```



#### 2.5.5.7 G1(Garbage-First)



> `官网`： https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/g1_gc.html#garbage_first_garbage_collection
>
> 使用G1收集器时，Java堆的内存布局与就与其他收集器有很大差别，它将整个Java堆划分为多个大小相等的独立区域（Region），虽然还保留有新生代和老年代的概念都采用**复制算法**，但新生代和老年代不再是物理隔离的了，它们都是一部分Region（不需要连续）的集合。
>
> 每个Region大小都是一样的，可以是1M到32M之间的数值，但是必须保证是2的n次幂
>
> 如果对象太大，一个Region放不下[超过Region大小的50%]，那么就会直接放到H中
>
> 设置Region大小：-XX:G1HeapRegionSize=<N>M
>
> 所谓Garbage-Frist，其实就是优先回收垃圾最多的Region区域
>
> ```
> （1）分代收集（仍然保留了分代的概念）
> （2）空间整合（整体上属于“标记-整理”算法，不会导致空间碎片）
> （3）可预测的停顿（比CMS更先进的地方在于能让使用者明确指定一个长度为M毫秒的时间片段内，消耗在垃圾收集上的时间不得超过N毫秒）
> ```

![image-20240117175314041](E:\图灵课堂\JVM\JVM专题.assets\image-20240117175314041.png)

工作过程可以分为如下几步

```
初始标记（Initial Marking）      标记以下GC Roots能够关联的对象，并且修改TAMS的值，需要暂停用户线程
并发标记（Concurrent Marking）   从GC Roots进行可达性分析，找出存活的对象，与用户线程并发执行
最终标记（Final Marking）        修正在并发标记阶段因为用户程序的并发执行导致变动的数据，需暂停用户线程
筛选回收（Live Data Counting and Evacuation） 对各个Region的回收价值和成本进行排序，根据用户所期望的GC停顿时间制定回收计划
```

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/da0055ec96e34eb8bf4206619b7f6abc.png)

JDK9及之后默认采用。优点在于延迟可控(-XX:MaxGCPauseMillis=毫秒数)，不会产生内存碎片，并发标记的SATB算法效率更高。

#### 2.5.5.8 ZGC

> `官网`： https://docs.oracle.com/en/java/javase/11/gctuning/z-garbage-collector1.html#GUID-A5A42691-095E-47BA-B6DC-FB4E5FAA43D0
>
> JDK11新引入的ZGC收集器，ORACLE官方开发，不管是物理上还是逻辑上，ZGC中已经不存在新老年代的概念了
>
> 会分为一个个page，当进行GC操作时会对page进行压缩，因此没有碎片问题
>
> 只能在64位的linux上使用，目前用得还比较少

（1）可以达到10ms以内的停顿时间要求

（2）支持TB级别的内存

（3）堆内存变大后停顿时间还是在10ms以内

![image-20240117210205850](E:\图灵课堂\JVM\JVM专题.assets\image-20240117210205850.png)

关键特征：

1. 着色指针(Colored Pointer)

2. 读屏障(Load Barrier)

相比G1

![image-20240117221008580](E:\图灵课堂\JVM\JVM专题.assets\image-20240117221008580.png)

停顿时间更短，但时间内回收的垃圾可能较少。相同数量的垃圾可能耗时会更多。也就是说吞吐量会比G1差。



#### 2.5.5.9 垃圾收集器分类

* **串行收集器**->Serial和Serial Old

只能有一个垃圾回收线程执行，用户线程暂停。

`适用于内存比较小的嵌入式设备`。

* **并行收集器**[吞吐量优先]->Parallel Scanvenge、Parallel Old

多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态。

`适用于科学计算、后台处理等若交互场景`。

* **并发收集器**[停顿时间优先]->CMS、G1、ZGC

用户线程和垃圾收集线程同时执行(但并不一定是并行的，可能是交替执行的)，垃圾收集线程在执行的时候不会停顿用户线程的运行。

`适用于相对时间有要求的场景，比如Web`。

#### 2.5.5.10 常见问题

* 吞吐量和停顿时间

  * 停顿时间->垃圾收集器 `进行` 垃圾回收终端应用执行响应的时间
  * 吞吐量->运行用户代码时间/(运行用户代码时间+垃圾收集时间)

  ```
  停顿时间越短就越适合需要和用户交互的程序，良好的响应速度能提升用户体验；
  高吞吐量则可以高效地利用CPU时间，尽快完成程序的运算任务，主要适合在后台运算而不需要太多交互的任务。
  ```

  `小结`:这两个指标也是评价垃圾回收器好处的标准。

* 如何选择合适的垃圾收集器

  > https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/collectors.html#sthref28

  * 优先调整堆的大小让服务器自己来选择
  * 如果内存小于100M，使用串行收集器
  * 如果是单核，并且没有停顿时间要求，使用串行或JVM自己选
  * 如果允许停顿时间超过1秒，选择并行或JVM自己选
  * 如果响应时间最重要，并且不能超过1秒，使用并发收集器

* 对于G1收集

JDK 7开始使用，JDK 8非常成熟，JDK 9默认的垃圾收集器，适用于新老生代。

是否使用G1收集器？

```
（1）50%以上的堆被存活对象占用
（2）对象分配和晋升的速度变化非常大
（3）垃圾回收时间比较长
```

* G1中的RSet

全称Remembered Set，记录维护Region中对象的引用关系

```
试想，在G1垃圾收集器进行新生代的垃圾收集时，也就是Minor GC，假如该对象被老年代的Region中所引用，这时候新生代的该对象就不能被回收，怎么记录呢？
不妨这样，用一个类似于hash的结构，key记录region的地址，value表示引用该对象的集合，这样就能知道该对象被哪些老年代的对象所引用，从而不能回收。
```

* 如何开启需要的垃圾收集器

> 这里JVM参数信息的设置大家先不用关心，后面会学习到。

```
（1）串行
	-XX：+UseSerialGC 
	-XX：+UseSerialOldGC
（2）并行(吞吐量优先)：
    -XX：+UseParallelGC
    -XX：+UseParallelOldGC
（3）并发收集器(响应时间优先)
	-XX：+UseConcMarkSweepGC
	-XX：+UseG1GC
```

#### 最后汇总图：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1651836084092/46aa5ed8a0be44f58bd47fdff5283e28.png)







# CMS垃圾收集器深入解析

回顾一下上节课的CMS的内容

## CMS回收流程

> `官网`： https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/cms.html#concurrent_mark_sweep_cms_collector
>
> CMS(Concurrent Mark Sweep)收集器是一种以获取 `最短回收停顿时间`为目标的收集器。
>
> 采用的是"标记-清除算法",整个过程分为4步

```
(1)初始标记 CMS initial mark     标记GC Roots直接关联对象，不用Tracing，速度很快
(2)并发标记 CMS concurrent mark  进行GC Roots Tracing
(3)重新标记 CMS remark           修改并发标记因用户程序变动的内容
(4)并发清除 CMS concurrent sweep 清除不可达对象回收空间，同时有新垃圾产生，留着下次清理称为浮动垃圾
```

> 由于整个过程中，并发标记和并发清除，收集器线程可以与用户线程一起工作，所以总体上来说，CMS收集器的内存回收过程是与用户线程一起并发地执行的。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/de9c7c9d366e4326ab7750baee284cc4.png)

```
优点：并发收集、低停顿
缺点：产生大量空间碎片、并发阶段会降低吞吐量
```

## 第一个问题：为什么我的CMS回收流程图上初始标记是单线程，为什么不使用多线程呢？

> 初始化标记阶段是串行的，这是JDK7的行为。JDK8以后默认是并行的，可以通过参数
>
> -XX:+CMSParallelInitialMarkEnabled控制

## CMS的两种模式与一种特殊策略：

### **Backgroud CMS** ：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/9ec440cd9a54415497b697ca7c2a5db5.png)

实际上我们的并发标记还能被整理成两个流程

(1)初始标记
(2)并发标记
(3)并发预处理
(4)可中止的预处理
(5)重新标记
(6)并发清除

为什么我们的并发标记细化之后还会额外有两个流程出现呢？

讨论这个问题之前，我们先思考一个问题，假设CMS要进行老年代的垃圾回收，我们如何判断被年轻代的对象引用的老年代对象是可达对象。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/849954d22a56413d9b522ff94a83a1d9.png)

**也就是这张图，当老年代被回收的时候，我们如何判断A对象是存活对象。**

答：必须扫描新生代来确定，所以CMS虽然是老年代的垃圾回收器，却需要扫描新生代的原因。

问题2：既然这个时候我需要扫描新生代，那么全量扫描会不会很慢

答：肯定会的 ，但是接踵而来的问题：既然会很慢，我们的停顿时间很长，可是CMS的目标是什么

CMS(Concurrent Mark Sweep)收集器是一种以获取 `最短回收停顿时间`为目标的收集器。这不是与他的设计理念不一致吗？

**思考：怎么让我们的回收变快**

答：肯定是垃圾越少越快。所以我们的CMS想到了一种方式，就是我先进行新生代的垃圾回收，也就是一次young GC，回收完毕之后。是不是我们新生代的对象就变少了，那么我再进行垃圾回收，是不是就变快了。

所以，CMS有两个参数：

**CMSScheduleRemarkEdenSizeThreshold             默认值：2M**

**CMSScheduleRemarkEdenPenetration              默认值：50%**

这两个参数组合起来就是预清理之后，Eden空间使用超过2M的时候启动可中断的并发预清理（CMS-concurrent-abortable-preclean），到Eden空间使用率到达50%的时候中断（但不是结束），进入Remark（重新标记阶段）。

这里面有个概念，那为什么并发预处理前面会有可中断几个字呢？什么意思。

可中断意味着，假设你一直在预处理，预处理是干什么，无非就是去帮你把正式应该处理的前置工作给做了。所以他一定干了很多事情，但是这些事情迟早有个头，所以就设置了一个时间对他进行打断。所以，并发预处理的逻辑是当你发生了minor GC  ，我就预处理结束了。

**这里有个问题，我怎么知道你什么时候发生minor  GC**

答：答案是我不知道，垃圾回收是JVM自动调度的，所以我们无法控制垃圾回收。

那我不可能无限制的执行下去，总要有个结束时间吧

CMS提供了一个参数**CMSMaxAbortablePrecleanTime** ，默认为5S

> 只要到了5S，不管发没发生Minor GC，有没有到CMSScheduleRemardEdenPenetration都会中止此阶段，进入remark。

如果在5S内还是没有执行Minor GC怎么办？

> CMS提供CMSScavengeBeforeRemark参数，使remark前强制进行一次Minor GC。

到这里，新生代策略已经聊完了

接下来老年代的话，有几个重要的点先给大家聊一下。

### 记忆集

当我们进行young gc时，我们的**gc roots除了常见的栈引用、静态变量、常量、锁对象、class对象**这些常见的之外，如果 **老年代有对象引用了我们的新生代对象** ，那么老年代的对象也应该加入gc roots的范围中，但是如果每次进行young gc我们都需要扫描一次老年代的话，那我们进行垃圾回收的代价实在是太大了，因此我们引入了一种叫做记忆集的抽象数据结构来记录这种引用关系。

记忆集是一种用于记录从非收集区域指向收集区域的指针集合的数据结构。

```
如果我们不考虑效率和成本问题，我们可以用一个数组存储所有有指针指向新生代的老年代对象。但是如果这样的话我们维护成本就很好，打个比方，假如所有的老年代对象都有指针指向了新生代，那么我们需要维护整个老年代大小的记忆集，毫无疑问这种方法是不可取的。因此我们引入了卡表的数据结构
```

### 卡表

记忆集是我们针对于跨代引用问题提出的思想，而卡表则是针对于该种思想的具体实现。（可以理解为记忆集是结构，卡表是实现类）

[1字节，00001000，1字节，1字节]

```
在hotspot虚拟机中，卡表是一个字节数组，数组的每一项对应着内存中的某一块连续地址的区域，如果该区域中有引用指向了待回收区域的对象，卡表数组对应的元素将被置为1，没有则置为0；
```

(1)  卡表是使用一个字节数组实现:CARD_TABLE[],每个元素对应着其标识的内存区域一块特定大小的内存块,称为"卡页"。hotSpot使用的卡页是2^9大小,即512字节

(2)  一个卡页中可包含多个对象,只要有一个对象的字段存在跨代指针,其对应的卡表的元素标识就变成1,表示该元素变脏,否则为0。GC时,只要筛选本收集区的卡表中变脏的元素加入GC Roots里。

卡表的使用图例

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/e428c317c70e4f01945955cd7f93a4d9.png)

并发标记的时候，A对象发生了所在的引用发生了变化，所以A对象所在的块被标记为脏卡

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/ca90c6933c49450eba6bbb085a90bada.png)

继续往下到了重新标记阶段，修改对象的引用，同时清除脏卡标记。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1652270310036/007dd60e3a58435a9880aede2b95b47e.png)

**卡表其他作用：**

老年代识别新生代的时候

对应的card table被标识为相应的值（card table中是一个byte，有八位，约定好每一位的含义就可区分哪个是引用新生代，哪个是并发标记阶段修改过的）

### **Foregroud CMS：**

其实这个也是CMS一种收集模式，但是他是并发失败才会走的模式。这里聊到一个概念，什么是并发失败呢？

并发失败官方的描述是：

> 如果 **并发搜集器不能在年老代填满之前完成不可达（unreachable）对象的回收** ，或者 **年老代中有效的空闲内存空间不能满足某一个内存的分配请求** ，此时应用会被暂停，并在此暂停期间开始垃圾回收，直到回收完成才会恢复应用程序。这种无法并发完成搜集的情况就成为 **并发模式失败（concurrent mode failure）** ，而且这种情况的发生也意味着我们需要调节并发搜集器的参数了。

简单来说，也就是我去进行并发标记的时候，内存不够了，这个时候我会进入STW，并且开始全局Full GC.

那么什么时候会进行并发失败呢   换句话说，我们的难道非要满了之后才进行收集

```
-XX:CMSInitiatingOccupancyFraction  
-XX:+UseCMSInitiatingOccupancyOnly

注意：-XX:+UseCMSInitiatingOccupancyOnly 只是用设定的回收阈值(上面指定的70%),如果不指定,JVM仅在第一次使用设定值,后续则自动调整.这两个参数表示只有在Old区占了CMSInitiatingOccupancyFraction设置的百分比的内存时才满足触发CMS的条件。注意这只是满足触发CMS GC的条件。至于什么时候真正触发CMS GC，由一个后台扫描线程决定。CMSThread默认2秒钟扫描一次，判断是否需要触发CMS，这个参数可以更改这个扫描时间间隔。
```

看源码公式：

((100 - MinHeapFreeRatio) + (double)( CMSTriggerRatio * MinHeapFreeRatio) / 100.0) / 100.0

也就是按照默认值

**当老年代达到** ((100 - 40) + (double) 80 * 40 / 100 ) / 100 = 92 %时，会触发CMS回收。

> 如何避免他的出现：
>
> 为了尽量避免并发模式失败发生，我们可以调节-XX:CMSInitiatingOccupancyFraction=<N>参数，去控制当年老代的内存占用达到多少的时候（N%），便开启并发搜集器开始回收年老代。

### **CMS的标记压缩算法-----MSC（**Mark Sweep Compact**）**

他的回收方式其实就是我们的滑动整理，并且进行整理的时候一般都是两个参数

```
-XX:+UseCMSCompactAtFullCollection 
-XX:CMSFullGCsBeforeCompaction=0
这两个参数表示多少次FullGC后采用MSC算法压缩堆内存，0表示每次FullGC后都会压缩，同时0也是默认值
```

碎片问题也是CMS采用的标记清理算法最让人诟病的地方：Backgroud CMS采用的标记清理算法会导致内存碎片问题，从而埋下发生FullGC导致长时间STW的隐患。

所以如果触发了FullGC，无论是否会采用MSC算法压缩堆，那都是ParNew+CMS组合非常糟糕的情况。因为这个时候并发模式已经搞不定了，而且整个过程单线程，完全STW，可能会压缩堆（是否压缩堆通过上面两个参数控制），真的不能再糟糕了！想象如果这时候业务量比较大，由于FullGC导致服务完全暂停几秒钟，甚至上10秒，对用户体验影响得多大。

### 三色标记

为了让垃圾回收器标记和清理的时候不发生STW，引入并发标记和清理。

因为标记期间应用线程还在继续跑，对象间的引用可能发生变化，多标和漏标的情况就有可能发生。

这里引入“三色标记”主要用来标记内存中存活和需要回收的对象。（CMS，G1都用到三色标记法）

来给大家解释下，把Gcroots可达性分析遍历对象过程中遇到的对象， 按照“是否访问过”这个条件标记成以下三种颜色：

**黑色：**

```
表示对象已经被垃圾收集器访问过， 且这个对象的所有引用都已经扫描过。 黑色的对象代表已经扫描过， 它是安全存活的， 如果有其他对象引用指向了黑色对象， 无须重新扫描一遍。 黑色对象不可能直接（不经过灰色对象） 指向某个白色对象。
```

**灰色：**

```
表示对象已经被垃圾收集器访问过， 但这个对象上至少存在一个引用还没有被扫描过。
```

**白色:**

```
表示对象尚未被垃圾收集器访问过。 显然在可达性分析刚刚开始的阶段， 所有的对象都是白色的， 若在分析结束的阶段， 仍然是白色的对象， 即代表不可达。
```

**标记过程：**

1.初始时，所有对象都在 【白色集合】中；

2.将GC Roots 直接引用到的对象 挪到 【灰色集合】中；

3.判断灰色集合中对象是否存在子引用：

​		存在，将本对象 引用到的 其他对象 全部挪到 【灰色集合】中；

​		不存在，将本对象 挪到 【黑色集合】里面。

重复步骤3，直至【灰色集合】为空时结束。

结束后，仍在【白色集合】的对象即为GC Roots 不可达，可以进行回收

![image-20240118214112200](E:\图灵课堂\JVM\JVM专题.assets\image-20240118214112200.png)



**存在问题：**

**多标-浮动垃圾**

```
在并发标记过程中，如果由于方法运行结束导致部分局部变量(gcroot)被销毁，这个gcroot引用的对象之前又被扫描过 (被标记为非垃圾对象)，那么本轮GC不会回收这部分内存。这部分本应该回收但是没有回收到的内存，被称之为“浮动 垃圾”。浮动垃圾并不会影响垃圾回收的正确性，只是需要等到下一轮垃圾回收中才被清除。

另外，针对并发标记(还有并发清理)开始后产生的新对象，通常的做法是直接全部当成黑色，本轮不会进行清除。这部分 对象期间可能也会变为垃圾，这也算是浮动垃圾的一部分。
```

**漏标-读写屏障**

漏标只有**同时满足**以下两个条件时才会发生：

```
条件一：灰色对象 断开了 白色对象的引用；即灰色对象 原来成员变量的引用 发生了变化。

条件二：黑色对象 重新引用了 该白色对象；即黑色对象 成员变量增加了 新的引用。
```

漏标会导致被引用的对象被当成垃圾误删除，这是严重bug，必须解决

**解决方案：CMS采用写屏障+增量更新（Incremental Update）   G1采用写屏障+原始快照（Snapshot At The Beginning，SATB）    ZGC采用写屏障：   ** 。

**增量更新**就是当黑色对象**插入新的指向**白色对象的引用关系时， 就将这个新插入的引用记录下来， 等并发扫描结束之后， 再将这些记录过的引用关系中的黑色对象为根， 重新扫描一次。 这可以简化理解为， 黑色对象一旦新插入了指向白色对象的引用之后， 它就变回灰色对象了。

**原始快照**就是当灰色对象要**删除指向**白色对象的引用关系时， 就将这个要删除的引用记录下来， 在并发扫描结束之后， 再将这些记录过的引用关系中的灰色对象为根， 重新扫描一次，这样就能扫描到白色的对象，将白色对象直接标记为黑色(目的就是让这种对象在本轮gc清理中能存活下来，待下一轮gc的时候重新扫描，这个对象也有可能是浮动垃圾)

以上**无论是对引用关系记录的插入还是删除， 虚拟机的记录操作都是通过写屏障实现的。**

**写屏障实现原始快照（SATB）：** 当对象B的成员变量的引用发生变化时，比如引用消失（a.b.d = null），我们可以利用写屏障，将B原来成员变量的引用对象D记录下来：

**写屏障实现增量更新：** 当对象A的成员变量的引用发生变化时，比如新增引用（a.d = d），我们可以利用写屏障，将A新的成员变量引用对象D 记录下来：

### CMS标记清除的全局整理：

由于CMS使用的是标记清除算法，而标记清除算法会有大量的内存碎片的产生，所以JVM提供了

**-XX:+UseCMSCompactAtFullCollection**参数用于在全局GC（full GC）后进行一次碎片整理的工作，

由于每次全局GC后都进行碎片整理会较大的影响停顿时间，JVM又提供了参数

**-XX:CMSFullGCsBeforeCompaction**去 **控制在几次全局GC后会进行碎片整理** 。

## CMS常用参数含义：

**-XX:+UseConcMarkSweepGC**

打开CMS GC收集器。JVM在1.8之前默认使用的是Parallel GC，9以后使用G1 GC。

**-XX:+UseParNewGC**

当使用CMS收集器时，默认年轻代使用多线程并行执行垃圾回收（UseConcMarkSweepGC开启后则默认开启）。

**-XX:+CMSParallelRemarkEnabled**

采用并行标记方式降低停顿（默认开启）。

**-XX:+CMSConcurrentMTEnabled**

被启用时，并发的CMS阶段将以多线程执行（因此，多个GC线程会与所有的应用程序线程并行工作）。（默认开启）

**-XX:ConcGCThreads**

定义并发CMS过程运行时的线程数。

**-XX:ParallelGCThreads**

定义CMS过程并行收集的线程数。

**-XX:CMSInitiatingOccupancyFraction**

该值代表老年代堆空间的使用率，默认值为68。当老年代使用率达到此值之后，并行收集器便开始进行垃圾收集，该参数需要配合UseCMSInitiatingOccupancyOnly一起使用，单独设置无效。

**-XX:+UseCMSInitiatingOccupancyOnly**

该参数启用后，参数CMSInitiatingOccupancyFraction才会生效。默认关闭。

**-XX:+CMSClassUnloadingEnabled**

相对于并行收集器，CMS收集器默认不会对永久代进行垃圾回收。如果希望对永久代进行垃圾回收，可用设置-XX:+CMSClassUnloadingEnabled。默认关闭。

**-XX:+CMSIncrementalMode**

开启CMS收集器的增量模式。增量模式使得回收过程更长，但是暂停时间往往更短。默认关闭。

**-XX:CMSFullGCsBeforeCompaction**

设置在执行多少次Full GC后对内存空间进行压缩整理，默认值0。

**-XX:+CMSScavengeBeforeRemark**

在cms gc remark之前做一次ygc，减少gc roots扫描的对象数，从而提高remark的效率，默认关闭。

**-XX:+ExplicitGCInvokesConcurrent**

该参数启用后JVM无论什么时候调用系统GC，都执行CMS GC，而不是Full GC。

**-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses**

该参数保证当有系统GC调用时，永久代也被包括进CMS垃圾回收的范围内。

**-XX:+DisableExplicitGC**

该参数将使JVM完全忽略系统的GC调用（不管使用的收集器是什么类型）。

**-XX:+UseCompressedOops**

这个参数用于对类对象数据进行压缩处理，提高内存利用率。（默认开启）

**-XX:MaxGCPauseMillis=200**

这个参数用于设置GC暂停等待时间，单位为毫秒，不要设置过低。

## CMS的线程数计算公式

区分young区的parnew gc线程数和old区的cms线程数，分别为以下两参数：

* -XX:ParallelGCThreads=m       // STW暂停时使用的GC线程数，一般用满CPU
* -XX:ConcGCThreads=n            // GC线程和业务线程并发执行时使用的GC线程数，一般较小

### ParallelGCThreads

其中ParallelGCThreads 参数的默认值是：

* CPU核心数 <= 8，则为 ParallelGCThreads=CPU核心数，比如4C8G取4，8C16G取8
* CPU核心数 > 8，则为 ParallelGCThreads = CPU核心数 * 5/8 + 3 向下取整
* 16核的情况下，ParallelGCThreads = 13
* 32核的情况下，ParallelGCThreads = 23
* 64核的情况下，ParallelGCThreads = 43
* 72核的情况下，ParallelGCThreads = 48

**ConcGCThreads**

ConcGCThreads的默认值则为：

ConcGCThreads = (ParallelGCThreads + 3)/4 向下取整。

* ParallelGCThreads = 1~4时，ConcGCThreads = 1
* ParallelGCThreads = 5~8时，ConcGCThreads = 2
* ParallelGCThreads = 13~16时，ConcGCThreads = 4

## CMS推荐配置参数：

第一种情况：8C16G左右服务器，再大的服务器可以上G1了  没必要

```
-Xmx12g -Xms12g
-XX:ParallelGCThreads=8
-XX:ConcGCThreads=2
-XX:+UseConcMarkSweepGC
-XX:+CMSClassUnloadingEnabled
-XX:+CMSIncrementalMode
-XX:+CMSScavengeBeforeRemark
-XX:+UseCMSInitiatingOccupancyOnly
-XX:CMSInitiatingOccupancyFraction=70
-XX:CMSFullGCsBeforeCompaction=5
-XX:MaxGCPauseMillis=100  // 按业务情况来定
-XX:+ExplicitGCInvokesConcurrent
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses
-XX:+PrintGCTimeStamps
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
```

第二种情况：4C8G

```
-Xmx6g -Xms6g
-XX:ParallelGCThreads=4
-XX:ConcGCThreads=1
-XX:+UseConcMarkSweepGC
-XX:+CMSClassUnloadingEnabled
-XX:+CMSIncrementalMode
-XX:+CMSScavengeBeforeRemark
-XX:+UseCMSInitiatingOccupancyOnly
-XX:CMSInitiatingOccupancyFraction=70
-XX:CMSFullGCsBeforeCompaction=5
-XX:MaxGCPauseMillis=100  // 按业务情况来定
-XX:+ExplicitGCInvokesConcurrent
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses
-XX:+PrintGCTimeStamps
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
```

第三种情况：2C4G，这种情况下，也不推荐使用，因为2C的情况下，线程上下文的开销比较大，性能可能还不如你不动的情况，没必要。非要用，给你个配置，你自己玩。

```
-Xmx3g -Xms3g
-XX:ParallelGCThreads=2
-XX:ConcGCThreads=1
-XX:+UseConcMarkSweepGC
-XX:+CMSClassUnloadingEnabled
-XX:+CMSIncrementalMode
-XX:+CMSScavengeBeforeRemark
-XX:+UseCMSInitiatingOccupancyOnly
-XX:CMSInitiatingOccupancyFraction=70
-XX:CMSFullGCsBeforeCompaction=5
-XX:MaxGCPauseMillis=100  // 按业务情况来定
-XX:+ExplicitGCInvokesConcurrent
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses
-XX:+PrintGCTimeStamps
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
```

# G1垃圾收集器深入解析

加入一条索引（记录）的源码的工作流程图如下：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1653290970029/4ed0e0512cfd4ebd9b7e71ca9f21bc14.png)

### **CSet（Collection Set 回收集合）**

收集集合(CSet)代表每次GC暂停时回收的一系列目标分区。在任意一次收集暂停中，CSet所有分区都会被释放，内部存活的对象都会被转移到分配的空闲分区中。因此无论是年轻代收集，还是混合收集，工作的机制都是一致的。年轻代收集CSet只容纳年轻代分区，而混合收集会通过启发式算法，在老年代候选回收分区中，筛选出回收收益最高的分区添加到CSet中。

CSet根据两种不同的回收类型分为两种不同CSet。
1.CSet of Young Collection
2.CSet of Mix Collection
CSet of Young Collection 只专注回收 Young Region 跟 Survivor Region ，而CSet of Mix Collection 模式下的CSet 则会通过RSet计算Region中对象的活跃度，

活跃度阈值-XX:G1MixedGCLiveThresholdPercent(默认85%)，只有活跃度高于这个阈值的才会准入CSet，混合模式下CSet还可以通过-XX:G1OldCSetRegionThresholdPercent(默认10%)设置，CSet跟整个堆的比例的数量上限。

### App Thread （用户线程）

这个很简单，App thread 就是执行一个java程序的业务逻辑，实际运行的一些线程。

### Concurrence Refinement Thread（同步优化线程）

这个线程主要用来处理代间引用之间的关系用的。当赋值语句发生后，G1通过Writer Barrier技术，跟G1自己的筛选算法，筛选出此次索引赋值是否是跨区（Region）之间的引用。如果是跨区索引赋值，在线程的内存缓冲区写一条log，一旦日志缓冲区写满，就重新起一块缓冲重新写，而原有的缓冲区则进入全局缓冲区。

Concurrence Refinement Thread 扫描全局缓冲区的日志，根据日志更新各个区（Region）的RSet。这块逻辑跟后面讲到的SATB技术十分相似，但又不同SATB技术主要更新的是存活对象的位图。

Concurrence Refinement Thread（同步优化线程） 可通过

**-XX:G1ConcRefinementThreads (默认等于-XX:ParellelGCThreads)设置。**

如果发现全局缓冲区日志积累较多，G1会调用更多的线程来出来缓冲区日志，甚至会调用App Thread 来处理，造成应用任务堵塞，所以必须要尽量避免这样的现象出现。可以通过阈值

**-XX:G1ConcRefinementGreenZone**

**-XX:G1ConcRefinementYellowZone**

**-XX:G1ConcRefinementRedZone**

这三个参数来设置G1调用线程的数量来处理全局缓存的积累的日志。

## G1垃圾收集器的三种模式

### young GC

young GC的触发条件

Eden区的大小范围 = [ -XX:G1NewSizePercent, -XX:G1MaxNewSizePercent ] = [ 整堆5%, 整堆60% ]
在[ 整堆5%, 整堆60% ]的基础上，G1会计算下现在Eden区回收大概要多久时间，如果回收时间远远小于参数-XX:MaxGCPauseMills设定的值（默认200ms），那么增加年轻代的region，继续给新对象存放，不会马上做YoungGC。
G1计算回收时间接近参数-XX:MaxGCPauseMills设定的值，那么就会触发YoungGC。

![16532909700293019829ffy](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1/7050bfc14cb14683bdc5f7578dbf5c43.png)

#### **具体步骤：**

##### 根扫描：

GC并行任务包括根扫描、更新RSet、对象复制，主要逻辑在g1CollectedHeap.cpp G1ParTask类的work方法中；evacuate_roots方法为根扫描。

```C++
void work(uint worker_id) {
    if (worker_id >= _n_workers) return;  // no work needed this round

    _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::GCWorkerStart, worker_id, os::elapsedTime());

    {
      ResourceMark rm;
      HandleMark   hm;

      ReferenceProcessor*             rp = _g1h->ref_processor_stw();

      G1ParScanThreadState            pss(_g1h, worker_id, rp);
      G1ParScanHeapEvacFailureClosure evac_failure_cl(_g1h, &pss, rp);

      pss.set_evac_failure_closure(&evac_failure_cl);

      bool only_young = _g1h->g1_policy()->gcs_are_young();

      // Non-IM young GC.
      G1ParCopyClosure<G1BarrierNone, G1MarkNone>             scan_only_root_cl(_g1h, &pss, rp);
      G1CLDClosure<G1MarkNone>                                scan_only_cld_cl(&scan_only_root_cl,
                                                                               only_young, // Only process dirty klasses.
                                                                               false);     // No need to claim CLDs.
      // IM young GC.
      //    Strong roots closures.
      G1ParCopyClosure<G1BarrierNone, G1MarkFromRoot>         scan_mark_root_cl(_g1h, &pss, rp);
      G1CLDClosure<G1MarkFromRoot>                            scan_mark_cld_cl(&scan_mark_root_cl,
                                                                               false, // Process all klasses.
                                                                               true); // Need to claim CLDs.
      //    Weak roots closures.
      G1ParCopyClosure<G1BarrierNone, G1MarkPromotedFromRoot> scan_mark_weak_root_cl(_g1h, &pss, rp);
      G1CLDClosure<G1MarkPromotedFromRoot>                    scan_mark_weak_cld_cl(&scan_mark_weak_root_cl,
                                                                                    false, // Process all klasses.
                                                                                    true); // Need to claim CLDs.

      OopClosure* strong_root_cl;
      OopClosure* weak_root_cl;
      CLDClosure* strong_cld_cl;
      CLDClosure* weak_cld_cl;

      bool trace_metadata = false;

      if (_g1h->g1_policy()->during_initial_mark_pause()) {
        // We also need to mark copied objects.
        strong_root_cl = &scan_mark_root_cl;
        strong_cld_cl  = &scan_mark_cld_cl;
        if (ClassUnloadingWithConcurrentMark) {
          weak_root_cl = &scan_mark_weak_root_cl;
          weak_cld_cl  = &scan_mark_weak_cld_cl;
          trace_metadata = true;
        } else {
          weak_root_cl = &scan_mark_root_cl;
          weak_cld_cl  = &scan_mark_cld_cl;
        }
      } else {
        strong_root_cl = &scan_only_root_cl;
        weak_root_cl   = &scan_only_root_cl;
        strong_cld_cl  = &scan_only_cld_cl;
        weak_cld_cl    = &scan_only_cld_cl;
      }

      pss.start_strong_roots();

      _root_processor->evacuate_roots(strong_root_cl,
                                      weak_root_cl,
                                      strong_cld_cl,
                                      weak_cld_cl,
                                      trace_metadata,
                                      worker_id);

      G1ParPushHeapRSClosure push_heap_rs_cl(_g1h, &pss);
      _root_processor->scan_remembered_sets(&push_heap_rs_cl,
                                            weak_root_cl,
                                            worker_id);
      pss.end_strong_roots();

      {
        double start = os::elapsedTime();
        G1ParEvacuateFollowersClosure evac(_g1h, &pss, _queues, &_terminator);
        evac.do_void();
        double elapsed_sec = os::elapsedTime() - start;
        double term_sec = pss.term_time();
        _g1h->g1_policy()->phase_times()->add_time_secs(G1GCPhaseTimes::ObjCopy, worker_id, elapsed_sec - term_sec);
        _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::Termination, worker_id, term_sec);
        _g1h->g1_policy()->phase_times()->record_thread_work_item(G1GCPhaseTimes::Termination, worker_id, pss.term_attempts());
      }
      _g1h->g1_policy()->record_thread_age_table(pss.age_table());
      _g1h->update_surviving_young_words(pss.surviving_young_words()+1);

      if (ParallelGCVerbose) {
        MutexLocker x(stats_lock());
        pss.print_termination_stats(worker_id);
      }

      assert(pss.queue_is_empty(), "should be empty");

      // Close the inner scope so that the ResourceMark and HandleMark
      // destructors are executed here and are included as part of the
      // "GC Worker Time".
    }
    _g1h->g1_policy()->phase_times()->record_time_secs(G1GCPhaseTimes::GCWorkerEnd, worker_id, os::elapsedTime());
  }
};
```

**g1RootProcessor.cpp的evacuate_roots主要逻辑如下**：

```java
void G1RootProcessor::evacuate_roots(OopClosure* scan_non_heap_roots,
                                     OopClosure* scan_non_heap_weak_roots,
                                     CLDClosure* scan_strong_clds,
                                     CLDClosure* scan_weak_clds,
                                     bool trace_metadata,
                                     uint worker_i) {
  // First scan the shared roots.
  double ext_roots_start = os::elapsedTime();
  G1GCPhaseTimes* phase_times = _g1h->g1_policy()->phase_times();

  BufferingOopClosure buf_scan_non_heap_roots(scan_non_heap_roots);
  BufferingOopClosure buf_scan_non_heap_weak_roots(scan_non_heap_weak_roots);

  OopClosure* const weak_roots = &buf_scan_non_heap_weak_roots;
  OopClosure* const strong_roots = &buf_scan_non_heap_roots;

  // CodeBlobClosures are not interoperable with BufferingOopClosures
  G1CodeBlobClosure root_code_blobs(scan_non_heap_roots);

  process_java_roots(strong_roots,
                     trace_metadata ? scan_strong_clds : NULL,
                     scan_strong_clds,
                     trace_metadata ? NULL : scan_weak_clds,
                     &root_code_blobs,
                     phase_times,
                     worker_i);

  // This is the point where this worker thread will not find more strong CLDs/nmethods.
  // Report this so G1 can synchronize the strong and weak CLDs/nmethods processing.
  if (trace_metadata) {
    worker_has_discovered_all_strong_classes();
  }

  process_vm_roots(strong_roots, weak_roots, phase_times, worker_i);
  process_string_table_roots(weak_roots, phase_times, worker_i);
  {
    // Now the CM ref_processor roots.
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::CMRefRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_refProcessor_oops_do)) {
      // We need to treat the discovered reference lists of the
      // concurrent mark ref processor as roots and keep entries
      // (which are added by the marking threads) on them live
      // until they can be processed at the end of marking.
      _g1h->ref_processor_cm()->weak_oops_do(&buf_scan_non_heap_roots);
    }
  }

  if (trace_metadata) {
    {
      G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::WaitForStrongCLD, worker_i);
      // Barrier to make sure all workers passed
      // the strong CLD and strong nmethods phases.
      wait_until_all_strong_classes_discovered();
    }

    // Now take the complement of the strong CLDs.
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::WeakCLDRoots, worker_i);
    ClassLoaderDataGraph::roots_cld_do(NULL, scan_weak_clds);
  } else {
    phase_times->record_time_secs(G1GCPhaseTimes::WaitForStrongCLD, worker_i, 0.0);
    phase_times->record_time_secs(G1GCPhaseTimes::WeakCLDRoots, worker_i, 0.0);
  }

  // Finish up any enqueued closure apps (attributed as object copy time).
  buf_scan_non_heap_roots.done();
  buf_scan_non_heap_weak_roots.done();

  double obj_copy_time_sec = buf_scan_non_heap_roots.closure_app_seconds()
      + buf_scan_non_heap_weak_roots.closure_app_seconds();

  phase_times->record_time_secs(G1GCPhaseTimes::ObjCopy, worker_i, obj_copy_time_sec);

  double ext_root_time_sec = os::elapsedTime() - ext_roots_start - obj_copy_time_sec;

  phase_times->record_time_secs(G1GCPhaseTimes::ExtRootScan, worker_i, ext_root_time_sec);

  // During conc marking we have to filter the per-thread SATB buffers
  // to make sure we remove any oops into the CSet (which will show up
  // as implicitly live).
  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::SATBFiltering, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_filter_satb_buffers) && _g1h->mark_in_progress()) {
      JavaThread::satb_mark_queue_set().filter_thread_buffers();
    }
  }

  _process_strong_tasks.all_tasks_completed();
}
```

重点在于三个方法：

//处理java根

process_java_roots(closures, phase_times, worker_i);

//处理JVM根
process_vm_roots(closures, phase_times, worker_i);

//处理String table根
process_string_table_roots(closures, phase_times, worker_i);

###### 处理java根

* 处理所有已加载类的元数据
* 处理所有Java线程当前栈帧的引用和虚拟机内部线程

```java
void G1RootProcessor::process_java_roots(OopClosure* strong_roots,
                                         CLDClosure* thread_stack_clds,
                                         CLDClosure* strong_clds,
                                         CLDClosure* weak_clds,
                                         CodeBlobClosure* strong_code,
                                         G1GCPhaseTimes* phase_times,
                                         uint worker_i) {
  assert(thread_stack_clds == NULL || weak_clds == NULL, "There is overlap between those, only one may be set");
  // 在CLDG上迭代，线程早就已经完成了，先让我们处理强的CLD跟N方法，遇到问题之后处理弱的
  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::CLDGRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_ClassLoaderDataGraph_oops_do)) {
      ClassLoaderDataGraph::roots_cld_do(strong_clds, weak_clds);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::ThreadRoots, worker_i);
    Threads::possibly_parallel_oops_do(strong_roots, thread_stack_clds, strong_code);
  }
}
```

###### 处理JVM根

* 处理JVM内部使用的引用（Universe和SystemDictionary）
* 处理JNI句柄
* 处理对象锁的引用
* 处理java.lang.management管理和监控相关类的引用
* 处理JVMTI（JVM Tool Interface）的引用
* 处理AOT静态编译的引用

```Java
void G1RootProcessor::process_vm_roots(OopClosure* strong_roots,
                                       OopClosure* weak_roots,
                                       G1GCPhaseTimes* phase_times,
                                       uint worker_i) {
  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::UniverseRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_Universe_oops_do)) {
      Universe::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::JNIRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_JNIHandles_oops_do)) {
      JNIHandles::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::ObjectSynchronizerRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_ObjectSynchronizer_oops_do)) {
      ObjectSynchronizer::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::FlatProfilerRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_FlatProfiler_oops_do)) {
      FlatProfiler::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::ManagementRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_Management_oops_do)) {
      Management::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::JVMTIRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_jvmti_oops_do)) {
      JvmtiExport::oops_do(strong_roots);
    }
  }

  {
    G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::SystemDictionaryRoots, worker_i);
    if (!_process_strong_tasks.is_task_claimed(G1RP_PS_SystemDictionary_oops_do)) {
      SystemDictionary::roots_oops_do(strong_roots, weak_roots);
    }
  }
}
```

###### 处理String table根

* 处理StringTable JVM字符串哈希表的引用

```java
void G1RootProcessor::process_string_table_roots(OopClosure* weak_roots, G1GCPhaseTimes* phase_times,
                                                 uint worker_i) {
  assert(weak_roots != NULL, "Should only be called when all roots are processed");

  G1GCParPhaseTimesTracker x(phase_times, G1GCPhaseTimes::StringTableRoots, worker_i);
  // All threads execute the following. A specific chunk of buckets
  // from the StringTable are the individual tasks.
  StringTable::possibly_parallel_oops_do(weak_roots);
}
```

##### 对象复制

* 判断对象是否在CSet中，如是则判断对象是否已经copy过了
* 如果已经copy过，则直接找到新对象
* 如果没有copy过，则调用copy_to_survivor_space函数copy对象到survivor区
* 修改老对象的对象头，指向新对象地址，并将锁标志位置为11

```java
void G1ParCopyClosure<barrier, do_mark_object>::do_oop_work(T* p) {
  T heap_oop = oopDesc::load_heap_oop(p);

  if (oopDesc::is_null(heap_oop)) {
    return;
  }

  oop obj = oopDesc::decode_heap_oop_not_null(heap_oop);

  assert(_worker_id == _par_scan_state->queue_num(), "sanity");

  const InCSetState state = _g1->in_cset_state(obj);
  if (state.is_in_cset()) {
    oop forwardee;
    markOop m = obj->mark();
    if (m->is_marked()) {
      forwardee = (oop) m->decode_pointer();
    } else {
      forwardee = _par_scan_state->copy_to_survivor_space(state, obj, m);
    }
    assert(forwardee != NULL, "forwardee should not be NULL");
    oopDesc::encode_store_heap_oop(p, forwardee);
    if (do_mark_object != G1MarkNone && forwardee != obj) {
      // If the object is self-forwarded we don't need to explicitly
      // mark it, the evacuation failure protocol will do so.
      mark_forwarded_object(obj, forwardee);
    }

    if (barrier == G1BarrierKlass) {
      do_klass_barrier(p, forwardee);
    }
  } else {
    if (state.is_humongous()) {
      _g1->set_humongous_is_live(obj);
    }
    // The object is not in collection set. If we're a root scanning
    // closure during an initial mark pause then attempt to mark the object.
    if (do_mark_object == G1MarkFromRoot) {
      mark_object(obj);
    }
  }

  if (barrier == G1BarrierEvac) {
    _par_scan_state->update_rs(_from, p, _worker_id);
  }
}
```

###### **copy_to_survivor_space函数**

**copy_to_survivor_space在g1ParScanThreadState.cpp中**

* 根据age判断copy到新生代还是老年代
* 先尝试在PLAB中分配对象
* PLAB分配失败后的逻辑与TLAB类似，先申请一个新的PLAB，在旧PLAB中填充dummy对象，在新PLAB中分配，如果还是失败，则在新生代Region中直接分配
* 如果还是失败，则尝试在老年代Region中重新分配
* age加1，由于锁升级机制，当对象锁状态是轻量级锁或重量级锁时，对象头被修改为指向栈锁记录的指针或者互斥量的指针，修改age需要特殊处理
* 对于字符串去重的处理
* 如果是数组，且数组长度超过ParGCArrayScanChunk（默认50）时，将对象放入队列而不是深度搜索栈中，防止搜索时溢出

```java
oop G1ParScanThreadState::copy_to_survivor_space(InCSetState const state,
                                                 oop const old,
                                                 markOop const old_mark) {
  const size_t word_sz = old->size();
  HeapRegion* const from_region = _g1h->heap_region_containing(old);
  // +1 to make the -1 indexes valid...
  const int young_index = from_region->young_index_in_cset()+1;
  assert( (from_region->is_young() && young_index >  0) ||
         (!from_region->is_young() && young_index == 0), "invariant" );

  uint age = 0;
  InCSetState dest_state = next_state(state, old_mark, age);
  // The second clause is to prevent premature evacuation failure in case there
  // is still space in survivor, but old gen is full.
  if (_old_gen_is_full && dest_state.is_old()) {
    return handle_evacuation_failure_par(old, old_mark);
  }
  HeapWord* obj_ptr = _plab_allocator->plab_allocate(dest_state, word_sz);

  // PLAB allocations should succeed most of the time, so we'll
  // normally check against NULL once and that's it.
  if (obj_ptr == NULL) {
    bool plab_refill_failed = false;
    obj_ptr = _plab_allocator->allocate_direct_or_new_plab(dest_state, word_sz, &plab_refill_failed);
    if (obj_ptr == NULL) {
      obj_ptr = allocate_in_next_plab(state, &dest_state, word_sz, plab_refill_failed);
      if (obj_ptr == NULL) {
        // This will either forward-to-self, or detect that someone else has
        // installed a forwarding pointer.
        return handle_evacuation_failure_par(old, old_mark);
      }
    }
    if (_g1h->_gc_tracer_stw->should_report_promotion_events()) {
      // The events are checked individually as part of the actual commit
      report_promotion_event(dest_state, old, word_sz, age, obj_ptr);
    }
  }

  assert(obj_ptr != NULL, "when we get here, allocation should have succeeded");
  assert(_g1h->is_in_reserved(obj_ptr), "Allocated memory should be in the heap");

#ifndef PRODUCT
  // Should this evacuation fail?
  if (_g1h->evacuation_should_fail()) {
    // Doing this after all the allocation attempts also tests the
    // undo_allocation() method too.
    _plab_allocator->undo_allocation(dest_state, obj_ptr, word_sz);
    return handle_evacuation_failure_par(old, old_mark);
  }
#endif // !PRODUCT

  // We're going to allocate linearly, so might as well prefetch ahead.
  Prefetch::write(obj_ptr, PrefetchCopyIntervalInBytes);

  const oop obj = oop(obj_ptr);
  const oop forward_ptr = old->forward_to_atomic(obj, old_mark, memory_order_relaxed);
  if (forward_ptr == NULL) {
    Copy::aligned_disjoint_words((HeapWord*) old, obj_ptr, word_sz);

    if (dest_state.is_young()) {
      if (age < markOopDesc::max_age) {
        age++;
      }
      if (old_mark->has_displaced_mark_helper()) {
        // In this case, we have to install the mark word first,
        // otherwise obj looks to be forwarded (the old mark word,
        // which contains the forward pointer, was copied)
        obj->set_mark_raw(old_mark);
        markOop new_mark = old_mark->displaced_mark_helper()->set_age(age);
        old_mark->set_displaced_mark_helper(new_mark);
      } else {
        obj->set_mark_raw(old_mark->set_age(age));
      }
      _age_table.add(age, word_sz);
    } else {
      obj->set_mark_raw(old_mark);
    }

    if (G1StringDedup::is_enabled()) {
      const bool is_from_young = state.is_young();
      const bool is_to_young = dest_state.is_young();
      assert(is_from_young == _g1h->heap_region_containing(old)->is_young(),
             "sanity");
      assert(is_to_young == _g1h->heap_region_containing(obj)->is_young(),
             "sanity");
      G1StringDedup::enqueue_from_evacuation(is_from_young,
                                             is_to_young,
                                             _worker_id,
                                             obj);
    }

    _surviving_young_words[young_index] += word_sz;

    if (obj->is_objArray() && arrayOop(obj)->length() >= ParGCArrayScanChunk) {
      // We keep track of the next start index in the length field of
      // the to-space object. The actual length can be found in the
      // length field of the from-space object.
      arrayOop(obj)->set_length(0);
      oop* old_p = set_partial_array_mask(old);
      do_oop_partial_array(old_p);
    } else {
      G1ScanInYoungSetter x(&_scanner, dest_state.is_young());
      obj->oop_iterate_backwards(&_scanner);
    }
    return obj;
  } else {
    _plab_allocator->undo_allocation(dest_state, obj_ptr, word_sz);
    return forward_ptr;
  }
}

```

##### 深度搜索复制

* 并行线程处理完当前任务后，可以窃取其他线程没有处理完的对象

```java
void G1ParEvacuateFollowersClosure::do_void() {
  EventGCPhaseParallel event;
  G1ParScanThreadState* const pss = par_scan_state();
  pss->trim_queue();
  event.commit(GCId::current(), pss->worker_id(), G1GCPhaseTimes::phase_name(_phase));
  do {
    EventGCPhaseParallel event;
    pss->steal_and_trim_queue(queues());
    event.commit(GCId::current(), pss->worker_id(), G1GCPhaseTimes::phase_name(_phase));
  } while (!offer_termination());
}
```

**具体复制逻辑在trim_queue函数中**

* 调用do_oop_evac复制一般对象，调用do_oop_partial_array处理大数组对象
* 如果对象已经复制，则无需再次复制
* 否则，调用copy_to_survivor_space复制对象
* 更新引用者field地址
* 如果引用者与当前对象不在同一个分区，且引用者不在新生代分区中，则更新RSet信息入队

```java
inline void G1ParScanThreadState::trim_queue_to_threshold(uint threshold) {
  StarTask ref;
  // Drain the overflow stack first, so other threads can potentially steal.
  while (_refs->pop_overflow(ref)) {
    if (!_refs->try_push_to_taskqueue(ref)) {
      dispatch_reference(ref);
    }
  }

  while (_refs->pop_local(ref, threshold)) {
    dispatch_reference(ref);
  }
}

inline void G1ParScanThreadState::deal_with_reference(oop* ref_to_scan) {
  if (!has_partial_array_mask(ref_to_scan)) {
    do_oop_evac(ref_to_scan);
  } else {
    do_oop_partial_array(ref_to_scan);
  }
}

template <class T> void G1ParScanThreadState::do_oop_evac(T* p) {
  // Reference should not be NULL here as such are never pushed to the task queue.
  oop obj = RawAccess<IS_NOT_NULL>::oop_load(p);

  // Although we never intentionally push references outside of the collection
  // set, due to (benign) races in the claim mechanism during RSet scanning more
  // than one thread might claim the same card. So the same card may be
  // processed multiple times, and so we might get references into old gen here.
  // So we need to redo this check.
  const InCSetState in_cset_state = _g1h->in_cset_state(obj);
  // References pushed onto the work stack should never point to a humongous region
  // as they are not added to the collection set due to above precondition.
  assert(!in_cset_state.is_humongous(),
         "Obj " PTR_FORMAT " should not refer to humongous region %u from " PTR_FORMAT,
         p2i(obj), _g1h->addr_to_region((HeapWord*)obj), p2i(p));

  if (!in_cset_state.is_in_cset()) {
    // In this case somebody else already did all the work.
    return;
  }

  markOop m = obj->mark_raw();
  if (m->is_marked()) {
    obj = (oop) m->decode_pointer();
  } else {
    obj = copy_to_survivor_space(in_cset_state, obj, m);
  }
  RawAccess<IS_NOT_NULL>::oop_store(p, obj);

  assert(obj != NULL, "Must be");
  if (HeapRegion::is_in_same_region(p, obj)) {
    return;
  }
  HeapRegion* from = _g1h->heap_region_containing(p);
  if (!from->is_young()) {
    enqueue_card_if_tracked(p, obj);
  }
}
```

### mixed   GC

混合式回收主要分为如下子阶段：

* 初始标记子阶段
* 并发标记子阶段
* 再标记子阶段
* 清理子阶段
* 垃圾回收

#### 是否进入并发标记判定

**g1Policy.cpp**

* YGC最后阶段判断是否启动并发标记
* 判断的依据是分配和即将分配的内存占比是否大于阈值
* 阈值受JVM参数InitiatingHeapOccupancyPercent控制，默认45

```java
G1IHOPControl* G1Policy::create_ihop_control(const G1Predictions* predictor){
  if (G1UseAdaptiveIHOP) {
    return new G1AdaptiveIHOPControl(InitiatingHeapOccupancyPercent,
                                     predictor,
                                     G1ReservePercent,
                                     G1HeapWastePercent);
  } else {
    return new G1StaticIHOPControl(InitiatingHeapOccupancyPercent);
  }
}


bool G1Policy::need_to_start_conc_mark(const char* source, size_t alloc_word_size) {
  if (about_to_start_mixed_phase()) {
    return false;
  }

  size_t marking_initiating_used_threshold = _ihop_control->get_conc_mark_start_threshold();

  size_t cur_used_bytes = _g1h->non_young_capacity_bytes();
  size_t alloc_byte_size = alloc_word_size * HeapWordSize;
  size_t marking_request_bytes = cur_used_bytes + alloc_byte_size;

  bool result = false;
  if (marking_request_bytes > marking_initiating_used_threshold) {
    result = collector_state()->in_young_only_phase() && !collector_state()->in_young_gc_before_mixed();
    log_debug(gc, ergo, ihop)("%s occupancy: " SIZE_FORMAT "B allocation request: " SIZE_FORMAT "B threshold: " SIZE_FORMAT "B (%1.2f) source: %s",
                              result ? "Request concurrent cycle initiation (occupancy higher than threshold)" : "Do not request concurrent cycle initiation (still doing mixed collections)",
                              cur_used_bytes, alloc_byte_size, marking_initiating_used_threshold, (double) marking_initiating_used_threshold / _g1h->capacity() * 100, source);
  }

  return result;
}

void G1Policy::record_new_heap_size(uint new_number_of_regions) {
  // re-calculate the necessary reserve
  double reserve_regions_d = (double) new_number_of_regions * _reserve_factor;
  // We use ceiling so that if reserve_regions_d is > 0.0 (but
  // smaller than 1.0) we'll get 1.
  _reserve_regions = (uint) ceil(reserve_regions_d);

  _young_gen_sizer->heap_size_changed(new_number_of_regions);

  _ihop_control->update_target_occupancy(new_number_of_regions * HeapRegion::GrainBytes);
}



```

如果需要进行并发标记，则通知并发标记线程

**g1CollectedHeap.cpp**

```java
void G1CollectedHeap::do_concurrent_mark() {
  MutexLockerEx x(CGC_lock, Mutex::_no_safepoint_check_flag);
  if (!_cm_thread->in_progress()) {
    _cm_thread->set_started();
    CGC_lock->notify();
  }
}
```

G1比CMS快的原因关键在于：写屏障+SATB算法比CMS处理并发标记和并发统计存活对象更高效

先回顾下G1的工作过程可以分为如下几步

```
初始标记（Initial Marking）      标记以下GC Roots能够关联的对象，并且修改TAMS的值，需要暂停用户线程
并发标记（Concurrent Marking）   从GC Roots进行可达性分析，找出存活的对象，与用户线程并发执行
最终标记（Final Marking）        修正在并发标记阶段因为用户程序的并发执行导致变动的数据，需暂停用户线程
筛选回收（Live Data Counting and Evacuation） 对各个Region的回收价值和成本进行排序，根据用户所期望的GC停顿时间制定回收计划
```

接下来我们详细分析下：

### SATB算法：

1，标记开始之前先会生成快照，记录标记开始bottom和top指针。

2然后开始初始标记 根可达（采用三色标记法）此时暂停用户线程但只标记根可达用时很短

3，接下来并发标记，继续扫描灰色的引用完成大部分存活对象的标记工作（在标记位图next上进行）

此时可能用户线程会新生成对象

​		此时就需要用到SATB的用处就来了。在并发标记的过程中，新生成的对象都会被标记成黑色（可存活）。根据top指针位置判断新生成，并会将top指针向前移至最新对象的位置，并不会为新增的对象创建新的位图。

​	（这就是其比CMS增量标记算法快的原因之一：因为SATB算法新增的都是可存活判断简单粗暴；而增量标记算法需要判断每次创建对象或更改引用的时候，对应的card会被重置成dirty ，需要重根集合开始扫描这些dirty card判断其引用看是否可存活

​		当然快也会有不足。因为这样判断存活，可能属于多标了原本不该继续存活的了。只能等到下轮标记才能被清除。以空间换时间）

4，最后进行筛选回收

并发标记也可能新增已标记可存活（黑色）的关联引用，该如何解决呢？

这就需要深入了解下 写屏障了



### 写屏障：

g1SATBCardTableModRefBS.hpp 源码关键方法如下

```c++
//field 表示被修改对象所在的Region
//newVal 表示被写入的Region的值
template<class T>static void write_ref_field_pre_static(T* field,oop newVal) {
    //将该Region中所有对象加载到内存中
    T heap_oop = oopDesc::load_heap_oop(field);
    //如果并发标记的时候 对非空对象的引用做了修改 就放到队列 satb_mark_queue_set中
    if(!oopDesc::is_null(heap_oop)){
        //enqueue完成具体操作
        enqueue(oopDesc::decode_heap_oop(heap_oop));
    }
}



void G1SATBCardTableModRefBS::enqueue(oop pre_val) {
  // 必须是一个java对象而不是null
  assert(pre_vav->is_oop(true),"Error");
   //判断是否处于并发标记阶段，只有并发标记阶段才为执行后续逻辑
    //为了避免多线程抢占本地队列，为每个线程都分配了单独的queue队列，默认大小1KB
   if(!JavaThread::satb_mark_queue_set().is_active()) return;
   Thread* thr = Thread::current();
    //如果当前正在运行的线程是java线程
   if(thr->is_Java_thread()) {
      JavaThread* jt= (JavaThead*)thr;
       //将新的引用写入到本地队列中，每个java线程有一个独立定长的satb_mark_queue
      jt->satb_mark_queue().enqueue(pre_val);
    	//否则当前正在进行的线程为 JVM线程
   }else {
       MutexLockerEx x(Shared_SATB_Q_lock),Mutex::_no_safepoint_chek_flag);
    	//最终标记阶段或者1KB被写满时 用户线程的队列的标记的内容会被合并到全局的SATB队列中
       JavaThread::satb_mark_queue_set().shared_satb_queue()->enqueue(pre_val);
   }
}

```

总结：

为每个线程都分配了单独的queue队列，默认大小1KB

将新的引用写入到本地队列中

最终标记阶段或者1KB被写满时 用户线程的队列的标记的内容会被合并到全局的SATB队列中（灰色）

然后再按三色标记法将这些新的对象标记完成即可

### 



### 初始标记

* 初始标记子阶段需要STW。
* 混合式GC的根GC就是YGC的Survivor Region。
* 在GC并发线程组中，调用G1CMRootRegionScanTask

```java
void G1ConcurrentMark::scan_root_regions() {
  // scan_in_progress() will have been set to true only if there was
  // at least one root region to scan. So, if it's false, we
  // should not attempt to do any further work.
  if (root_regions()->scan_in_progress()) {
    assert(!has_aborted(), "Aborting before root region scanning is finished not supported.");

    _num_concurrent_workers = MIN2(calc_active_marking_workers(),
                                   // We distribute work on a per-region basis, so starting
                                   // more threads than that is useless.
                                   root_regions()->num_root_regions());
    assert(_num_concurrent_workers <= _max_concurrent_workers,
           "Maximum number of marking threads exceeded");

    G1CMRootRegionScanTask task(this);
    log_debug(gc, ergo)("Running %s using %u workers for %u work units.",
                        task.name(), _num_concurrent_workers, root_regions()->num_root_regions());
    _concurrent_workers->run_task(&task, _num_concurrent_workers);

    // It's possible that has_aborted() is true here without actually
    // aborting the survivor scan earlier. This is OK as it's
    // mainly used for sanity checking.
    root_regions()->scan_finished();
  }
}
```

#### G1CMRootRegionScanTask

* while循环遍历根Region列表
* 调用scan_root_region，扫描每个根Region

```java
class G1CMRootRegionScanTask : public AbstractGangTask {
  G1ConcurrentMark* _cm;
public:
  G1CMRootRegionScanTask(G1ConcurrentMark* cm) :
    AbstractGangTask("G1 Root Region Scan"), _cm(cm) { }

  void work(uint worker_id) {
    assert(Thread::current()->is_ConcurrentGC_thread(),
           "this should only be done by a conc GC thread");

    G1CMRootRegions* root_regions = _cm->root_regions();
    HeapRegion* hr = root_regions->claim_next();
    while (hr != NULL) {
      _cm->scan_root_region(hr, worker_id);
      hr = root_regions->claim_next();
    }
  }
};
```

* 执行闭包G1RootRegionScanClosure，遍历整个Region中的对象

#### **G1RootRegionScanClosure具体逻辑在g1OopClosures.inline.hpp**

```java
inline void G1RootRegionScanClosure::do_oop_work(T* p) {
  T heap_oop = RawAccess<MO_VOLATILE>::oop_load(p);
  if (CompressedOops::is_null(heap_oop)) {
    return;
  }
  oop obj = CompressedOops::decode_not_null(heap_oop);
  _cm->mark_in_next_bitmap(_worker_id, obj);
}
```

#### **G1RootRegionScanClosure**

* 调用mark_in_next_bitmap标记根Region中的对象

```java
inline void G1RootRegionScanClosure::do_oop_work(T* p) {
  T heap_oop = RawAccess<MO_VOLATILE>::oop_load(p);
  if (CompressedOops::is_null(heap_oop)) {
    return;
  }
  oop obj = CompressedOops::decode_not_null(heap_oop);
  _cm->mark_in_next_bitmap(_worker_id, obj);
}
```

### 并发标记

并发标记子阶段与Mutator同时进行。
并发标记的入口在G1CMConcurrentMarkingTask的work方法

* 调用do_marking_step进行并发标记
* G1ConcMarkStepDurationMillis JVM参数定义了每次并发标记的最大时长，默认10毫秒

```java
  void work(uint worker_id) {
    assert(Thread::current()->is_ConcurrentGC_thread(), "Not a concurrent GC thread");
    ResourceMark rm;

    double start_vtime = os::elapsedVTime();

    {
      SuspendibleThreadSetJoiner sts_join;

      assert(worker_id < _cm->active_tasks(), "invariant");

      G1CMTask* task = _cm->task(worker_id);
      task->record_start_time();
      if (!_cm->has_aborted()) {
        do {
          task->do_marking_step(G1ConcMarkStepDurationMillis,
                                true  /* do_termination */,
                                false /* is_serial*/);

          _cm->do_yield_check();
        } while (!_cm->has_aborted() && task->has_aborted());
      }
      task->record_end_time();
      guarantee(!task->has_aborted() || _cm->has_aborted(), "invariant");
    }

    double end_vtime = os::elapsedVTime();
    _cm->update_accum_task_vtime(worker_id, end_vtime - start_vtime);
  }

```

#### `void G1CMTask::do_marking_step`主要功能

* 处理STAB队列，STAB的处理模式与DCQS类似
* 扫描全部的灰色对象（没有被完全扫描所有分支的根对象），并对它们的每一个field进行递归并发标记
* 当前任务完成后，窃取其他队列的任务

### 重新标记

由于并发标记子阶段与Mutator应用同时执行，对象引用关系仍然有可能发生变化，因此需要再标记阶段STW后处理完成全部STAB。

再标记子阶段入口在G1CMRemarkTask

* 仍然调用do_marking_step函数处理，但是target time为1000000000毫秒，表示任何情况下都要执行完成

```java
class G1CMRemarkTask : public AbstractGangTask {
  G1ConcurrentMark* _cm;
public:
  void work(uint worker_id) {
    G1CMTask* task = _cm->task(worker_id);
    task->record_start_time();
    {
      ResourceMark rm;
      HandleMark hm;

      G1RemarkThreadsClosure threads_f(G1CollectedHeap::heap(), task);
      Threads::threads_do(&threads_f);
    }

    do {
      task->do_marking_step(1000000000.0 /* something very large */,
                            true         /* do_termination       */,
                            false        /* is_serial            */);
    } while (task->has_aborted() && !_cm->has_overflown());
    // If we overflow, then we do not want to restart. We instead
    // want to abort remark and do concurrent marking again.
    task->record_end_time();
  }

  G1CMRemarkTask(G1ConcurrentMark* cm, uint active_workers) :
    AbstractGangTask("Par Remark"), _cm(cm) {
    _cm->terminator()->reset_for_reuse(active_workers);
  }
};

```

### 并发清除

清理子阶段是指RSet清理、选择回收的Region等，但并不会复制对象和回收Region。清理子阶段仍然需要STW，入口在cleanup方法：

* G1UpdateRemSetTrackingAfterRebuild中将Region的RSet状态置为Complete
* 调用record_concurrent_mark_cleanup_end选择哪些Region需要回收

```java
void G1ConcurrentMark::cleanup() {
  assert_at_safepoint_on_vm_thread();

  // If a full collection has happened, we shouldn't do this.
  if (has_aborted()) {
    return;
  }

  G1Policy* g1p = _g1h->g1_policy();
  g1p->record_concurrent_mark_cleanup_start();

  double start = os::elapsedTime();

  verify_during_pause(G1HeapVerifier::G1VerifyCleanup, VerifyOption_G1UsePrevMarking, "Cleanup before");

  {
    GCTraceTime(Debug, gc, phases) debug("Update Remembered Set Tracking After Rebuild", _gc_timer_cm);
    G1UpdateRemSetTrackingAfterRebuild cl(_g1h);
    _g1h->heap_region_iterate(&cl);
  }

  if (log_is_enabled(Trace, gc, liveness)) {
    G1PrintRegionLivenessInfoClosure cl("Post-Cleanup");
    _g1h->heap_region_iterate(&cl);
  }

  verify_during_pause(G1HeapVerifier::G1VerifyCleanup, VerifyOption_G1UsePrevMarking, "Cleanup after");

  // We need to make this be a "collection" so any collection pause that
  // races with it goes around and waits for Cleanup to finish.
  _g1h->increment_total_collections();

  // Local statistics
  double recent_cleanup_time = (os::elapsedTime() - start);
  _total_cleanup_time += recent_cleanup_time;
  _cleanup_times.add(recent_cleanup_time);

  {
    GCTraceTime(Debug, gc, phases) debug("Finalize Concurrent Mark Cleanup", _gc_timer_cm);
    _g1h->g1_policy()->record_concurrent_mark_cleanup_end();
  }
}

```

#### **G1UpdateRemSetTrackingAfterRebuild**

* 调用G1RemSetTrackingPolicy的update_after_rebuild方法

```java
class G1UpdateRemSetTrackingAfterRebuild : public HeapRegionClosure {
  G1CollectedHeap* _g1h;
public:
  G1UpdateRemSetTrackingAfterRebuild(G1CollectedHeap* g1h) : _g1h(g1h) { }

  virtual bool do_heap_region(HeapRegion* r) {
    _g1h->g1_policy()->remset_tracker()->update_after_rebuild(r);
    return false;
  }
};

```

**update_after_rebuild在G1RemSetTrackingPolicy类中**

```java
void G1RemSetTrackingPolicy::update_after_rebuild(HeapRegion* r) {
  assert(SafepointSynchronize::is_at_safepoint(), "should be at safepoint");

  if (r->is_old_or_humongous_or_archive()) {
    if (r->rem_set()->is_updating()) {
      assert(!r->is_archive(), "Archive region %u with remembered set", r->hrm_index());
      r->rem_set()->set_state_complete();
    }
//略去部分代码
}
```

* 将RSet状态置为Complete

#### **g1Policy.cpp**

* 调用CollectionSetChooser rebuild方法选择CSet
* 调用record_concurrent_mark_cleanup_end，判断CSet中可回收空间占比是否小于阈值

```java
void G1Policy::record_concurrent_mark_cleanup_end() {
  cset_chooser()->rebuild(_g1h->workers(), _g1h->num_regions());

  bool mixed_gc_pending = next_gc_should_be_mixed("request mixed gcs", "request young-only gcs");
  if (!mixed_gc_pending) {
    clear_collection_set_candidates();
    abort_time_to_mixed_tracking();
  }
  collector_state()->set_in_young_gc_before_mixed(mixed_gc_pending);
  collector_state()->set_mark_or_rebuild_in_progress(false);

  double end_sec = os::elapsedTime();
  double elapsed_time_ms = (end_sec - _mark_cleanup_start_sec) * 1000.0;
  _analytics->report_concurrent_mark_cleanup_times_ms(elapsed_time_ms);
  _analytics->append_prev_collection_pause_end_ms(elapsed_time_ms);

  record_pause(Cleanup, _mark_cleanup_start_sec, end_sec);
}
```

#### **collectionSetChooser.cpp**

* 使用ParKnownGarbageTask并行判断分区的垃圾情况
* 对Region继续排序，从order_regions函数可以看出，排序依据是gc_efficiency

```java
void CollectionSetChooser::rebuild(WorkGang* workers, uint n_regions) {
  clear();

  uint n_workers = workers->active_workers();

  uint chunk_size = calculate_parallel_work_chunk_size(n_workers, n_regions);
  prepare_for_par_region_addition(n_workers, n_regions, chunk_size);

  ParKnownGarbageTask par_known_garbage_task(this, chunk_size, n_workers);
  workers->run_task(&par_known_garbage_task);

  sort_regions();
}

void CollectionSetChooser::sort_regions() {
  // First trim any unused portion of the top in the parallel case.
  if (_first_par_unreserved_idx > 0) {
    assert(_first_par_unreserved_idx <= regions_length(),
           "Or we didn't reserved enough length");
    regions_trunc_to(_first_par_unreserved_idx);
  }
  _regions.sort(order_regions);
  assert(_end <= regions_length(), "Requirement");
#ifdef ASSERT
  for (uint i = 0; i < _end; i++) {
    assert(regions_at(i) != NULL, "Should be true by sorting!");
  }
#endif // ASSERT
  if (log_is_enabled(Trace, gc, liveness)) {
    G1PrintRegionLivenessInfoClosure cl("Post-Sorting");
    for (uint i = 0; i < _end; ++i) {
      HeapRegion* r = regions_at(i);
      cl.do_heap_region(r);
    }
  }
  verify();
}

static int order_regions(HeapRegion* hr1, HeapRegion* hr2) {
  if (hr1 == NULL) {
    if (hr2 == NULL) {
      return 0;
    } else {
      return 1;
    }
  } else if (hr2 == NULL) {
    return -1;
  }

  double gc_eff1 = hr1->gc_efficiency();
  double gc_eff2 = hr2->gc_efficiency();
  if (gc_eff1 > gc_eff2) {
    return -1;
  } if (gc_eff1 < gc_eff2) {
    return 1;
  } else {
    return 0;
  }
}
```

### **计算分区gc_efficiency逻辑在heapRegion.cpp**

* gc_efficiency=可回收的字节数 / 预计的回收毫秒数

```java
void HeapRegion::calc_gc_efficiency() {
  // GC efficiency is the ratio of how much space would be
  // reclaimed over how long we predict it would take to reclaim it.
  G1CollectedHeap* g1h = G1CollectedHeap::heap();
  G1Policy* g1p = g1h->g1_policy();

  // Retrieve a prediction of the elapsed time for this region for
  // a mixed gc because the region will only be evacuated during a
  // mixed gc.
  double region_elapsed_time_ms =
    g1p->predict_region_elapsed_time_ms(this, false /* for_young_gc */);
  _gc_efficiency = (double) reclaimable_bytes() / region_elapsed_time_ms;
}

```

#### **record_concurrent_mark_cleanup_end**

* 判断CSet中可回收空间占比是否小于阈值
* 阈值受JVM参数 G1HeapWastePercent控制，默认5。只有当可回收空间占比大于阈值时，才会启动混合式GC回收

```java
bool G1Policy::next_gc_should_be_mixed(const char* true_action_str,
                                       const char* false_action_str) const {
  if (cset_chooser()->is_empty()) {
    log_debug(gc, ergo)("%s (candidate old regions not available)", false_action_str);
    return false;
  }

  // Is the amount of uncollected reclaimable space above G1HeapWastePercent?
  size_t reclaimable_bytes = cset_chooser()->remaining_reclaimable_bytes();
  double reclaimable_percent = reclaimable_bytes_percent(reclaimable_bytes);
  double threshold = (double) G1HeapWastePercent;
  if (reclaimable_percent <= threshold) {
    log_debug(gc, ergo)("%s (reclaimable percentage not over threshold). candidate old regions: %u reclaimable: " SIZE_FORMAT " (%1.2f) threshold: " UINTX_FORMAT,
                        false_action_str, cset_chooser()->remaining_regions(), reclaimable_bytes, reclaimable_percent, G1HeapWastePercent);
    return false;
  }
  log_debug(gc, ergo)("%s (candidate old regions available). candidate old regions: %u reclaimable: " SIZE_FORMAT " (%1.2f) threshold: " UINTX_FORMAT,
                      true_action_str, cset_chooser()->remaining_regions(), reclaimable_bytes, reclaimable_percent, G1HeapWastePercent);
  return true;
}
```

### full GCjava

当晋升失败、疏散失败、大对象分配失败、Evac失败时，有可能触发Full GC。

JDK10之前，都是单线程，JDK10以及以后   多线程收集

#### 入口

Full GC的入口在g1CollectedHeap.cpp的G1CollectedHeap::do_full_collection

* 准备回收，prepare_collection
* 回收，collect
* 回收后处理，complete_collection

```java
bool G1CollectedHeap::do_full_collection(bool explicit_gc,
                                         bool clear_all_soft_refs) {
  assert_at_safepoint_on_vm_thread();

  if (GCLocker::check_active_before_gc()) {
    // Full GC was not completed.
    return false;
  }

  const bool do_clear_all_soft_refs = clear_all_soft_refs ||
      soft_ref_policy()->should_clear_all_soft_refs();

  G1FullCollector collector(this, explicit_gc, do_clear_all_soft_refs);
  GCTraceTime(Info, gc) tm("Pause Full", NULL, gc_cause(), true);

  collector.prepare_collection();
  collector.collect();
  collector.complete_collection();

  // Full collection was successfully completed.
  return true;
}

```

#### 准备阶段

* Full GC应当清理软引用
* 由于Full GC过程中，永久代（元空间）中的方法可能被移动，需要保存bcp字节码指针数据或者转化为bci字节码索引
* 保存轻量级锁和重量级锁的对象头
* 清理和处理对象的派生关系

```java
void G1FullCollector::prepare_collection() {
  _heap->g1_policy()->record_full_collection_start();

  _heap->print_heap_before_gc();
  _heap->print_heap_regions();

  _heap->abort_concurrent_cycle();
  _heap->verify_before_full_collection(scope()->is_explicit_gc());

  _heap->gc_prologue(true);
  _heap->prepare_heap_for_full_collection();

  reference_processor()->enable_discovery();
  reference_processor()->setup_policy(scope()->should_clear_soft_refs());

  // When collecting the permanent generation Method*s may be moving,
  // so we either have to flush all bcp data or convert it into bci.
  CodeCache::gc_prologue();

  // We should save the marks of the currently locked biased monitors.
  // The marking doesn't preserve the marks of biased objects.
  BiasedLocking::preserve_marks();

  // Clear and activate derived pointer collection.
  clear_and_activate_derived_pointers();
}

```

#### 回收阶段java

* phase1 并行标记对象
* phase2 并行准备压缩
* phase3 并行调整指针
* phase4 并行压缩

```java
void G1FullCollector::collect() {
  phase1_mark_live_objects();
  verify_after_marking();

  // Don't add any more derived pointers during later phases
  deactivate_derived_pointers();

  phase2_prepare_compaction();

  phase3_adjust_pointers();

  phase4_do_compaction();
}
```

#### 并行标记

从GC roots出发，递归标记所有的活跃对象。

* 标记对象，具体逻辑在G1FullGCMarkTask中
* 清理弱引用
* 卸载类的元数据（complete_cleaning）或仅清理字符串（partial_cleaning）
* 清理字符串会清理StringTable和字符串去重（JEP 192: String Deduplication in G1）

```c
void G1FullCollector::phase1_mark_live_objects() {
  // Recursively traverse all live objects and mark them.
  GCTraceTime(Info, gc, phases) info("Phase 1: Mark live objects", scope()->timer());

  // Do the actual marking.
  G1FullGCMarkTask marking_task(this);
  run_task(&marking_task);

  // Process references discovered during marking.
  G1FullGCReferenceProcessingExecutor reference_processing(this);
  reference_processing.execute(scope()->timer(), scope()->tracer());

  // Weak oops cleanup.
  {
    GCTraceTime(Debug, gc, phases) debug("Phase 1: Weak Processing", scope()->timer());
    WeakProcessor::weak_oops_do(_heap->workers(), &_is_alive, &do_nothing_cl, 1);
  }

  // Class unloading and cleanup.
  if (ClassUnloading) {
    GCTraceTime(Debug, gc, phases) debug("Phase 1: Class Unloading and Cleanup", scope()->timer());
    // Unload classes and purge the SystemDictionary.
    bool purged_class = SystemDictionary::do_unloading(scope()->timer());
    _heap->complete_cleaning(&_is_alive, purged_class);
  } else {
    GCTraceTime(Debug, gc, phases) debug("Phase 1: String and Symbol Tables Cleanup", scope()->timer());
    // If no class unloading just clean out strings.
    _heap->partial_cleaning(&_is_alive, true, G1StringDedup::is_enabled());
  }

  scope()->tracer()->report_object_count_after_gc(&_is_alive);
}

```

**G1FullGCMarkTask**

* 如果允许卸载类的元数据，则调用process_strong_roots；否则调用process_all_roots_no_string_table
* process_strong_roots的GC roots仅强根
* process_all_roots_no_string_table的GC roots包括弱根、强根，但是不含StringTable
* 遍历标记栈中的所有对象

```c
void G1FullGCMarkTask::work(uint worker_id) {
  Ticks start = Ticks::now();
  ResourceMark rm;
  G1FullGCMarker* marker = collector()->marker(worker_id);
  MarkingCodeBlobClosure code_closure(marker->mark_closure(), !CodeBlobToOopClosure::FixRelocations);

  if (ClassUnloading) {
    _root_processor.process_strong_roots(
        marker->mark_closure(),
        marker->cld_closure(),
        &code_closure);
  } else {
    _root_processor.process_all_roots_no_string_table(
        marker->mark_closure(),
        marker->cld_closure(),
        &code_closure);
  }

  // Mark stack is populated, now process and drain it.
  marker->complete_marking(collector()->oop_queue_set(), collector()->array_queue_set(), _terminator.terminator());

  // This is the point where the entire marking should have completed.
  assert(marker->oop_stack()->is_empty(), "Marking should have completed");
  assert(marker->objarray_stack()->is_empty(), "Array marking should have completed");
  log_task("Marking task", worker_id, start);
}

void G1RootProcessor::process_strong_roots(OopClosure* oops,
                                           CLDClosure* clds,
                                           CodeBlobClosure* blobs) {
  StrongRootsClosures closures(oops, clds, blobs);

  process_java_roots(&closures, NULL, 0);
  process_vm_roots(&closures, NULL, 0);

  _process_strong_tasks.all_tasks_completed(n_workers());
}

void G1RootProcessor::process_all_roots(OopClosure* oops,
                                        CLDClosure* clds,
                                        CodeBlobClosure* blobs,
                                        bool process_string_table) {
  AllRootsClosures closures(oops, clds);

  process_java_roots(&closures, NULL, 0);
  process_vm_roots(&closures, NULL, 0);

  if (process_string_table) {
    process_string_table_roots(&closures, NULL, 0);
  }
  process_code_cache_roots(blobs, NULL, 0);

  _process_strong_tasks.all_tasks_completed(n_workers());
}

void G1RootProcessor::process_all_roots(OopClosure* oops,
                                        CLDClosure* clds,
                                        CodeBlobClosure* blobs) {
  process_all_roots(oops, clds, blobs, true);
}

void G1RootProcessor::process_all_roots_no_string_table(OopClosure* oops,
                                                        CLDClosure* clds,
                                                        CodeBlobClosure* blobs) {
  assert(!ClassUnloading, "Should only be used when class unloading is disabled");
  process_all_roots(oops, clds, blobs, false);
}

```

#### 准备压缩

计算每个活跃对象应该在什么位置，即计算对象压缩后的新位置指针并写入对象头。

* 调用G1FullGCPrepareTask准备压缩
* 如果任务没有空闲Region，则调用prepare_serial_compaction串行合并所有线程的最后一个分区，以避免OOM

```c
void G1FullCollector::phase2_prepare_compaction() {
  GCTraceTime(Info, gc, phases) info("Phase 2: Prepare for compaction", scope()->timer());
  G1FullGCPrepareTask task(this);
  run_task(&task);

  // To avoid OOM when there is memory left.
  if (!task.has_freed_regions()) {
    task.prepare_serial_compaction();
  }
}

```

#### **G1FullGCPrepareTask**

* 压缩对象具体逻辑在G1FullGCCompactionPoint中实现，执行完成后，对象头存储了对象的新地址
* 如果是大对象分区，且对象已经都死亡，则直接释放分区

```c
void G1FullGCPrepareTask::work(uint worker_id) {
  Ticks start = Ticks::now();
  G1FullGCCompactionPoint* compaction_point = collector()->compaction_point(worker_id);
  G1CalculatePointersClosure closure(collector()->mark_bitmap(), compaction_point);
  G1CollectedHeap::heap()->heap_region_par_iterate_from_start(&closure, &_hrclaimer);

  // Update humongous region sets
  closure.update_sets();
  compaction_point->update();

  // Check if any regions was freed by this worker and store in task.
  if (closure.freed_regions()) {
    set_freed_regions();
  }
  log_task("Prepare compaction task", worker_id, start);
}

```

#### 调整指针

在上一步计算出所有活跃对象的新位置后，需要修改引用到新地址。

* 调整之前保存的轻量级锁和重量级锁对象的引用地址
* 调整弱根
* 调整全部根对象
* 处理字符串去重逻辑
* 一个region一个region的调整引用地址

```c
void G1FullCollector::phase3_adjust_pointers() {
  // Adjust the pointers to reflect the new locations
  GCTraceTime(Info, gc, phases) info("Phase 3: Adjust pointers", scope()->timer());

  G1FullGCAdjustTask task(this);
  run_task(&task);
}

```

```c
void G1FullGCAdjustTask::work(uint worker_id) {
  Ticks start = Ticks::now();
  ResourceMark rm;

  // Adjust preserved marks first since they are not balanced.
  G1FullGCMarker* marker = collector()->marker(worker_id);
  marker->preserved_stack()->adjust_during_full_gc();

  // Adjust the weak roots.

  if (Atomic::add(1u, &_references_done) == 1u) { // First incr claims task.
    G1CollectedHeap::heap()->ref_processor_stw()->weak_oops_do(&_adjust);
  }

  AlwaysTrueClosure always_alive;
  _weak_proc_task.work(worker_id, &always_alive, &_adjust);

  CLDToOopClosure adjust_cld(&_adjust, ClassLoaderData::_claim_strong);
  CodeBlobToOopClosure adjust_code(&_adjust, CodeBlobToOopClosure::FixRelocations);
  _root_processor.process_all_roots(
      &_adjust,
      &adjust_cld,
      &adjust_code);

  // Adjust string dedup if enabled.
  if (G1StringDedup::is_enabled()) {
    G1StringDedup::parallel_unlink(&_adjust_string_dedup, worker_id);
  }

  // Now adjust pointers region by region
  G1AdjustRegionClosure blk(collector()->mark_bitmap(), worker_id);
  G1CollectedHeap::heap()->heap_region_par_iterate_from_worker_offset(&blk, &_hrclaimer, worker_id);
  log_task("Adjust task", worker_id, start);
}

```

#### 移动对象

对象的新地址和引用都已经更新，现在需要把对象移动到新位置

* 具体压缩对象逻辑在G1FullGCCompactTask
* 如果phase2计算位置中使用了串行处理，则移动对象时也要使用串行处理移动每任务最后一个分区的对象

```c
void G1FullCollector::phase4_do_compaction() {
  // Compact the heap using the compaction queues created in phase 2.
  GCTraceTime(Info, gc, phases) info("Phase 4: Compact heap", scope()->timer());
  G1FullGCCompactTask task(this);
  run_task(&task);

  // Serial compact to avoid OOM when very few free regions.
  if (serial_compaction_point()->has_regions()) {
    task.serial_compaction();
  }
}

```

**G1FullGCCompactTask**

* 迭代处理每个Region
* 调用闭包G1CompactRegionClosure的apply函数移动对象到Region头部
* 如果Region中的全部对象都已清理，则回收该Region

```c
void G1FullGCCompactTask::work(uint worker_id) {
  Ticks start = Ticks::now();
  GrowableArray<HeapRegion*>* compaction_queue = collector()->compaction_point(worker_id)->regions();
  for (GrowableArrayIterator<HeapRegion*> it = compaction_queue->begin();
       it != compaction_queue->end();
       ++it) {
    compact_region(*it);
  }

  G1ResetHumongousClosure hc(collector()->mark_bitmap());
  G1CollectedHeap::heap()->heap_region_par_iterate_from_worker_offset(&hc, &_claimer, worker_id);
  log_task("Compaction task", worker_id, start);
}

size_t G1FullGCCompactTask::G1CompactRegionClosure::apply(oop obj) {
  size_t size = obj->size();
  HeapWord* destination = (HeapWord*)obj->forwardee();
  if (destination == NULL) {
    // Object not moving
    return size;
  }

  // copy object and reinit its mark
  HeapWord* obj_addr = (HeapWord*) obj;
  assert(obj_addr != destination, "everything in this pass should be moving");
  Copy::aligned_conjoint_words(obj_addr, destination, size);
  oop(destination)->init_mark_raw();
  assert(oop(destination)->klass() != NULL, "should have a class");

  return size;
}

```

整体垃圾回收图太大，加到附件中



# ZGC原理深入解析

> `官网`： [https://docs.oracle.com/en/java/javase/11/gctuning/z-garbage-collector1.html#GUID-A5A42691-095E-47BA-B6DC-FB4E5FAA43D0](https://docs.oracle.com/en/java/javase/11/gctuning/z-garbage-collector1.html#GUID-A5A42691-095E-47BA-B6DC-FB4E5FAA43D0)
>
> **ZGC（The Z Garbage Collector）是JDK 11中推出的一款追求极致低延迟的实验性质的垃圾收集器，不管是物理上还是逻辑上，在ZGC中已经不存在新老年代的概念了     pauseless  GC        C4    GC**
>
> **会分为一个个（Region）page，当进行GC操作时会对page进行压缩，因此没有碎片问题**
>
> 在JDK11只能在64位的linux上使用
>
> 个人认为用的少的原因:
>
> 1. 用JDK8以上的就不多，够用了是关键
> 2. 对于内存架构要求高
> 3. 在JDK15之前处于实验阶段

**（1）可以达到10ms以内的停顿时间要求**

**（2）支持TB级别的内存（支持8MB~4TB级别的堆，JDK15以后支持16TB）**

**（3）停顿时间不会随着堆的大小，或者活跃对象的大小而增加；堆内存变大后停顿时间还是在10ms以内**

![image-20240120191608334](E:\图灵课堂\JVM\JVM专题.assets\image-20240120191608334.png)



注意:初始标记，重新标记，初始转移都需要STW，但此时间都非常短暂。

初始标记仅标记GCRoot可达，重新标记只标记并发标记有变更引用的，初始转移只转移根对象相关的。

> 实际上:
>
> ZGC诞生于JDK11，经过不断的完善，JDK15中的ZGC已经不再是实验性质的了。
>
> 从只支持Linux/x64，到现在支持多平台；从不支持指针压缩，到支持压缩类指针…
>
> 在JDK16，ZGC将支持并发线程栈扫描（Concurrent Thread Stack Scanning），根据**SPECjbb2015测试**结果，实现并发线程栈扫描之后，ZGC的STW时间又能降低一个数量级，停顿时间将进入毫秒时代。
>
> **SPECjbb:** SPECjbb 是这几个字母的首字母组成的，**S**tandard **P**erformance **E**valuation **C**orporation（spec公司），**J**AVA server **B**usiness **B**enchmark（java服务器业务测试工具）。
>
> 在SPECjbb 这个基准测试中，被测产品要运行JVM，模拟一家全球大型零售企业的各种终端销售点请求、在线购买、数据挖掘等日常业务，通过不断增加的业务量来测试系统能够处理的最大值，同时会测试随着业务量增加，系统响应时间的变化，以全面评估运行各项Java业务应用的服务器性能水平。
>
> SPECjbb 模拟了三层客户/服务器模型结构：第一层是用户（客户端输入）；第二层是商业应用逻辑；第三层是数据库。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/91309132139b4185bdda58d7feff38ba.png)

## ZGC核心技术

### ZGC的内存布局

我们之前已经了解过G1把整个堆分成了大小相同的 region，每个堆大约可以有 2048 个region，每个 region 大小为 1~32 MB （必须是 2 的次方）

ZGC 的堆内存也是基于 Region 来分布，不过 ZGC 是不区分新生代老年代的。不同的是，ZGC 的 Region 支持动态地创建和销毁，并且 Region 的大小不是固定的，包括**三种类型的 Region** 

ZGC的Region分为三种：

1.Small Region 容量固定为2MB，用于存放小于256KB的对象。

2.Medium Region容量固定为32MB，用于存放大于等于256KB但不足4MB的对象。

3.Large Region 容量为2MB的整数倍，存放4MB及以上大小的对象，而且每个大型Region中只存放一个大对象。由于大对象移动代价过大，所以该对象不会被重分配。



### 多重映射

为了能更好的理解ZGC的内存管理，我们先看一下这个例子：

你在你爸爸妈妈眼中是儿子，在你女朋友眼中是男朋友。在全世界人面前就是最帅的人。你还有一个名字，但名字也只是你的一个代号，并不是你本人。将这个关系画一张映射图表示：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/45e6cb1b188b497388f4f6cb67429e5b.png)

在你爸爸的眼中，你就是儿子；
在你老婆的眼中，你就是老公；
在你同事的眼中，你就是同事；
假如你的名字是全世界唯一的，通过“你的名字”、“你爸爸的儿子”、“你老婆的老公”，“世界上最帅的人”最后定位到的都是你本人。

现在我们再来看看ZGC的内存管理。

ZGC为了能高效、灵活地管理内存，实现了两级内存管理：虚拟内存和物理内存，并且实现了物理内存和虚拟内存的映射关系。这和操作系统中虚拟地址和物理地址设计思路基本一致。

当应用程序创建对象时，首先在堆空间申请一个虚拟地址，ZGC同时会为该对象在Marked0、Marked1和Remapped三个视图空间分别申请一个虚拟地址，且这三个虚拟地址对应同一个物理地址。

内存多重映射，就是使用 [mmap](https://so.csdn.net/so/search?q=mmap&spm=1001.2101.3001.7020) 

![image-20240119215749919](E:\图灵课堂\JVM\JVM专题.assets\image-20240119215749919.png)

通过mmap函数 虚拟地址 找到对应的物理内存地址。

也就是把不同的虚拟内存地址映射到同一个物理内存地址上。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/f01df7b0647342488258314e249c954f.png)

图中的Marked0、Marked1和Remapped三个视图是什么意思呢？

ZGC 为了更灵活高效地管理内存，使用了内存多重映射，把同一块物理内存映射为 Marked0、Marked1 和 Remapped 三个虚拟内存。

当应用程序创建对象时，会在堆上申请一个虚拟地址，这时 ZGC 会为这个对象在 Marked0、Marked1 和 Remapped 这三个视图空间分别申请一个虚拟地址，这三个虚拟地址映射到同一个物理地址。

Marked0、Marked1 和 Remapped 这三个虚拟内存作为 ZGC 的三个视图空间，在同一个时间点内只能有一个有效。ZGC 就是通过这三个视图空间的切换，来完成并发的垃圾回收。


为什么这么设计呢？

这就是ZGC的高明之处，利用虚拟空间换时间，这三个空间的切换是由垃圾回收的不同阶段触发的，通过限定三个空间在同一时间点有且仅有一个空间有效高效的完成GC过程的并发操作，具体实现会在后面讲ZGC并发处理算法的部分再详细描述

### 读屏障

ZGC 通过利用读屏障而不是写入屏障，与HotSpot JVM中以前的GC (CMS，G1等) 算法显著不同。**读屏障解决了并发转移时对象指针更新问题**：在转移期间，如果移动对象而不用更新引用对象的传入指针（移动的对象可能被堆中的任何其他对象所引用），就会产生悬空指针 (已经被释放的内存空间或者无效的内存地址，访问悬空指针会出现问题) 。通过读屏障技术能够捕获此类悬空指针对象，并触发代码，更新对象的新位置，从而“修复”悬空指针。为了跟踪对象如何移动，以便在加载时固定悬空指针，ZGC中使用转发表 (forwarding tables ) 来将重定位前（旧）地址映射到重定位后（新）地址。无论是业务线程作为使用者访问对象，还是GC线程遍历堆中的所有活动对象（在标记期间）都有可能会触发读屏障。

ZGC读屏障如何实现呢？

举个例子，代码`var x = obj.field`。x是一个位于堆栈上的局部变量，field是一个位于堆上的指针。业务线程在操作堆对象时触发读屏障。读屏障的执行路径有快 (fast path) 和慢 (slow path) 两种，如果正在加载的指针有效状态 (good color) ，则采用加载屏障的快速路径，否则，采用慢速路径。快速路径实际上是空的，而慢速路径包含计算有效状态指针的逻辑：检查对象是否已经（或即将）重新定位，如果是，则查找或生成新的地址。读屏障除了能让触发读屏障的线程读取到最新地址，同时还具有**自我修复指针**（self-healed）的功能，这意味着读屏障会修改指针的状态，以便后续其他线程访问时能执行快速路径。无论采用哪条路径，都会返回正确状态的地址。

这里的读屏障实际上指的是一种手段，并且是一种类**似于AOP的手段**。

我们之前聊的写屏障是数据写入时候的屏障，而java内存屏障中的读屏障实际上也是类似的。

但是在**ZGC中的读屏障，则是JVM向应用代码插入一小段代码的技术，当应用线程从堆中读取对象引用时，就会执行这段代码**。他跟我们的java内存屏障中的读屏障根本就不是一个东西。他是在字**节码层面或者编译代码层面给读操作增加一个额外的处理**。一个类似面向切面的处理。

并且ZGC的读屏障是只有从中读取对象引用，才需要加入读屏障

**读屏障案例：**

```java
Object o = obj.FieldA      // 从堆中读取对象引用，需要加入读屏障
<load barrier needed here>
  
Object p = o               // 无需加入读屏障，因为不是从堆中读取引用
o.dosomething()            // 无需加入读屏障，因为不是从堆中读取引用
int i =  obj.FieldB        // 无需加入读屏障，因为不是对象引用
```

**那么我加上这个读屏障有什么作用呢？**

这里我们思考下：

由于GC线程和应用线程是并发执行的，所以肯定会存在应用线程去A对象内部的引用所指向的对象B的时候，这个对象B正在被GC线程移动或者其他操作。JDK1.8中必须STW不然会因为对象指针位置变化而找不到对象。

加上读屏障之后，应用线程会去探测对象B是否被GC线程操作，然后等待操作完成再读取对象，确保数据的准确性。这个操作强依赖于前面的多种映射。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/dfc79a3cd8f7416db53ec0e78a33097b.png)

具体探查操作图：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/ec4025d0c8a44b91a98ead2e4ff01a51.png)

问题：如此复杂的探查操作会不会影响程序的性能呢？

会，据测试，最多百分之4的性能损耗。但这是ZGC并发转移的基础，为了降低STW，设计者认为这点牺牲是可接受的。

### 指针染色

读屏障以及指针染色是我们能够实现并发转移的核心技术之一，也是关键所在。

这里可以回顾一下之前讲过的CMS和G1通过增量更新和SATB原始快照解决的漏标问题。

接下来看看染色指针。

**染色指针是一种将少量信息直接存储在指针上的技术**

我们都知道，之前的垃圾收集器都是把GC信息（标记信息、GC分代年龄…）存在对象头的Mark Word里。举个例子：

如果某个物品是个垃圾，就在这个物品上盖一个“垃圾”的章；如果这个物品不是垃圾了，就把这个物品上的“垃圾”印章洗掉。

而ZGC是这样做的：

如果某个物品是垃圾。就在这个物品的信息或者标签里面标注这个物品是个垃圾，以后不管这个物品在哪扫描，快递到哪，别人都知道他是个垃圾了。也许哪一天，这个物品不再是垃圾，比如收废品的小王，觉得比如这个物品有利用价值。就把这个物品标签信息里面的“垃圾”标志去掉。

在这例子中，“这个物品”就是一个对象，而“标签”就是指向这个对象的指针。

ZGC将信息存储在指针中，这种技术有一个高大上的名字——染色指针（Colored Pointer）

**原理：**
Linux下64位的虚拟指针的高18位不能用来寻址，但剩余的46位指针所能支持的64TB内存在今天仍然能够充分满足大型服务器的需要。鉴于此，ZGC的染色指针技术继续盯上了这剩下的46位指针宽度，将其高4位提取出来存储四个标志信息。通过这些标志位，虚拟机可以直接从指针中看到其引用对象的三色标记状态、是否进入了重分配集（即被移动过）、是否只能通过finalize()方法才能被访问到。当然，由于这些标志位进一步压缩了原本就只有46位的地址空间，也直接导致
ZGC能够管理的内存不可以超过4TB（2的42次幂)  当然，后续的版本可以了，因为开发了更多的位数。前面是觉得没必要，够大了。

而后续开发变成了这个样子：

![image-20240119205757761](E:\图灵课堂\JVM\JVM专题.assets\image-20240119205757761.png)

黄色Finalizable表示是否需要通过 finalize 方法来访问到 （预留）

蓝色Remapped表示是否进入了重分配集（即最开始和被移动过）

绿色Marked0表示标识过（三色状态），本次GC阶段

红色Marked1表示标识过（三色状态），上次GC阶段



### **详细解读：**

**一：初始标记**：这个阶段会 STW，仅标记 GC Root直接可达的对象，压到标记栈中；

**二：并发标记**根据初始标记的对象开始并发遍历对象图，还会统计每个 region 的存活对象的数量

因为GC 线程和 Java 应用线程会并行运行。这个过程需要注意下面几点：

​	1，GC 标记线程访问对象时，如果对象地址视图是 Remapped，就把对象地址视图切换到 Marked0，如果对象地址视图已经是 Marked0，说明已经被其他标记线程访问过了，跳过不处理。

​	2，标记过程中Java 应用线程新创建的对象会直接进入 Marked0 视图。

​	3，标记过程中Java 应用线程访问对象时，如果对象的地址视图是 Remapped，就把对象地址视图切换到 Marked0，可以参考前面讲的读屏障。

​	4，标记过程中Java 应用线程修改了某对象引用，则将引用对象移入Remapped，表示此对象需要重新分配（再标记来处理变动的）

​	5，标记结束后，如果对象地址视图是 Marked0，那就是活跃的，如果对象地址视图是 Remapped，那就是不活跃的。

并发标记采用两个视图是为了区分前一次标记和这一次标记。如果这次标记的视图是 Marked0，那下一次并发标记就会把视图切换到 Marked1。这样做可以配合 ZGC 按照页回收垃圾的做法。

在64位的机器中，对象指针是64位的。

* ZGC使用64位地址空间的第0~43位存储对象地址，2^44 = 16TB，所以ZGC最大支持16TB的堆。
* 而第44~47位作为颜色标志位，Marked0、Marked1和Remapped代表三个视图标志位，Finalizable表示这个对象只能通过finalizer才能访问。
* 第48~63位固定为0没有利用。





### ZGC 过程

前面已经讲过，ZGC 使用内存多重映射技术，把物理内存映射为 Marked0、Marked1 和 Remapped 三个地址视图，利用地址视图的切换，ZGC 实现了高效的并发收集。

ZGC 的垃圾收集过程包括标记、转移和重定位三个阶段。如下图：

![img](https://img-blog.csdnimg.cn/img_convert/1616a17a0409a129977982410117d40d.png)

- 初始化阶段：ZGC初始化之后，整个堆内存空间的地址视图被设置为 Remapped；
- 标记阶段：当进入标记阶段时，视图转变为 Marked0 或者 Marked1；
- 转移阶段：从标记阶段结束进入转移阶段时，视图再次被设置为 Remapped；

 

**ZGC执行周期**

如下图 所示，ZGC 周期由三个 STW 暂停和四个并发阶段组成：

标记/重新映射( M/R )、

并发引用处理( RP )、

并发转移准备( EC ) 

并发转移( RE )。

为了读者能快速理解，下面对ZGC执行过程进行了大量简化。

![img](https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2023%2F1020%2F208575a6j00s2tt5h003hd200u000itg00it00bs.jpg&thumbnail=660x2147483647&quality=80&type=jpg)

**初始标记(STW1)**

ZGC 初始标记执行包含三个主要任务。

1，地址视图被设置成M0 (或M1) ，M0还是M1根据前一周期交替设置的。

2，重新分配新的页面给业务线程创建对象，ZGC只会处理当前周期之前分配的页面。

3，初始标记只会存活的根对象被标记为M0 (M1) ，并被加入标记栈进行并发标记。





**GC周期中地址视图窗口**

![img](https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2023%2F1020%2F1e5bfe07j00s2tt5i001ad200u000afg00it006j.jpg&thumbnail=660x2147483647&quality=80&type=jpg)

**ZGC周期中状态窗口划分**

**并发标记(M/R)**

**并发标记的任务有2个：**

第一，并发标记线程从待标记的对象列表出发，根据对象引用关系图遍历对象的成员变量，递归进行标记。

第二，计算，并更新关联页面的活跃度信息。活动信息是页面上的活动字节数，用于选择将要回收的页面，这些对象将作为堆碎片整理的一部分进行重新定位。

这个阶段，GC线程和应用线程是并发执行的，根据初始标记的对象开始并发遍历对象图，还会统计每个 region 的存活对象的数量，具体流程如下图：

![img](https://img-blog.csdnimg.cn/img_convert/d6336c8992d689eddb174a08b59e4610.png)

**再标记阶段(STW2)**

再标记阶段的主要任务有3个：

执行修复任务，指线程运行C2编译的代码，在进入再标记阶段时可能发生漏标。

结束标记，并发标记后业务线程本地标记栈可能存在待标记的对象，执行本步骤的目的就是对这些待标记对象进行标记。

执行部分非强根并行标记。



**并发转移准备(EC)**

并发转移准备任务：

筛选所有可以被回收的页面

选择垃圾比较多的页面作为页面转移集



**初始转移(STW3)**

初始转移主要以下过程：

1，调整地址视图：将地址视图从M0或者M1调整为Remapped，说明进入真正的转移，此后所有分配的对象视图都是Remapped。

2，重定位TLAB：因为地址视图调整，所以要调整TLAB中地址的视图。

3，开始转移：从根集合出发，遍历根对象的直接引用的对象，对这些对象进行转移。





初始转移是STW的，其处理时间和GC Roots的数量成正比，一般情况耗时非常短。

**并发转移（RE）**

初始转移完成了GC Roots对象重定位，在并发转移阶段将对前面步骤确定的转移集 (EC) ，对转移集的每一页执行转移。

**转发**的作用是存储对转移后旧地址到新地址的映射，转发表的数据存储在页面中，转移完成的页面即可被回收掉。

并发转移完成之后整个ZGC周期完成。

这个阶段，GC线程和应用线程是并发执行的，GC线程和应用线程操作对象流程如下图：

![image-20240120203354701](E:\图灵课堂\JVM\JVM专题.assets\image-20240120203354701.png)

**ZGC算法演示**

为了说明ZGC算法，下图演示了示例中的所有阶段。

![img](https://nimg.ws.126.net/?url=http%3A%2F%2Fdingyue.ws.126.net%2F2023%2F1020%2F7115a79ej00s2tt5i002od200n0015fg00it00xv.jpg&thumbnail=660x2147483647&quality=80&type=jpg)



**图8：ZGC算法演示**

图8(1)显示了堆的初始状态，应用启动后ZGC完成了初始化。

在图8(2)中，选择M0作为全局标记，并且所有根指针都被标记成M0。然后，所有根都被推送到标记堆栈，该标记堆栈在并发标记 (M/R) 期间由GC线程消耗。

如图8(3)所示，图中用合适的颜色绘制对象本身，以表明它们已被标记，即使指针有状态。

在图8(4) 中，选择存活对象最少的页面（中间的页面）作为转移候选集 (EC) 。

随后，在图8(5)中，全局标记被设置为Remmaped，并且所有根指针都已更新Remmaped。如果根指向EC，则相应的对象将被重新定位，并且根指针更新为新地址。

在图8(6)中，EC中的对象被转移，并且地址记录被逐出页面中转发表上，用于新旧地址转换。当并发转移阶段结束时，当前GC周期也会结束。当前周期内整个EC都会被回收。这里可能有个疑问，对象的旧地址还没有更新，页面如果被回收了如何还能访问对象呢？原因是回收的是页面中对象存储空间，转发表不会被回收，如果此时业务线程访问这些对象，会触发读屏障的慢路径位，失效指针会被修复。对于没有访问到的失效指针，直到下一个GC并发标记 (M/R) 阶段才会被修复。

在图8(7)中，下一个GC循环开始，M1被选择为全局状态（M0 和 M1 之间交替使用）。

在图8(8)中，并发标记阶段 (M/R) 通过查询转发表失效的指标被映射到新位置。

最后，在图8(9)中，上一周期EC页面的转发表被回收，为即将到来的并发转移 (RE) 阶段做准备。



## ZGC并发处理算法

GC并发处理算法利用全局空间视图的切换和对象地址视图的切换，结合SATB算法实现了高效的并发。

相比于 Java 原有的百毫秒级的暂停的 Parallel GC 和 G1，以及未解决碎片化问题的 CMS ，并发和压缩式的 ZGC 可谓是 Java GC 能力的一次重大飞跃—— GC 线程在整理内存的同时，可以让 Java 线程继续执行。 ZGC 采用标记-压缩策略来回收 Java 堆：ZGC 首先会并发标记( concurrent mark )堆中的活跃对象，然后并发转移( concurrent relocate )将部分区域的活跃对象整理到一起。这里与早先的 Java GC 不同之处在于，目前 ZGC 是单代垃圾回收器，在标记阶段会遍历堆中的全部对象。

ZGC的并发处理算法三个阶段的全局视图切换如下：

初始化阶段：ZGC初始化之后，整个内存空间的地址视图被设置为Remapped
标记阶段：当进入标记阶段时的视图转变为Marked0（以下皆简称M0）或者Marked1（以下皆简称M1）
转移阶段：从标记阶段结束进入转移阶段时的视图再次设置为Remapped

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/e984fc2813214b48aa6afc5b45cda169.png)

### 标记阶段

标记阶段全局视图切换到M0视图。因为应用程序和标记线程并发执行，那么对象的访问可能来自标记线程和应用程序线程。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/0fdbce86bb644d078659970f4571a0bc.png)

在标记阶段结束之后，对象的地址视图要么是M0，要么是Remapped。

如果对象的地址视图是M0，说明对象是活跃的；
如果对象的地址视图是Remapped，说明对象是不活跃的，即对象所使用的内存可以被回收。
当标记阶段结束后，ZGC会把所有活跃对象的地址存到对象活跃信息表，活跃对象的地址视图都是M0。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/528cd40547e24c06a4faa0a2bbf53b77.png)

### 转移阶段

转移阶段切换到Remapped视图。因为应用程序和转移线程也是并发执行，那么对象的访问可能来自转移线程和应用程序线程。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/37d03238115941a8a6fd5eab2b971404.png)

至此，ZGC的一个垃圾回收周期中，并发标记和并发转移就结束了。

### **为何要设计M0和M1**

我们提到在标记阶段存在两个地址视图M0和M1，上面的算法过程显示只用到了一个地址视图，为什么设计成两个？简单地说是为了区别前一次标记和当前标记。

ZGC是按照页面进行部分内存垃圾回收的，也就是说当对象所在的页面需要回收时，页面里面的对象需要被转移，如果页面不需要转移，页面里面的对象也就不需要转移。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/075521da81b647babfe5cb4ff2eb2da7.png)

如图，这个对象在第二次GC周期开始的时候，地址视图还是M0。如果第二次GC的标记阶段还切到M0视图的话，就不能区分出对象是活跃的，还是上一次垃圾回收标记过的。这个时候，第二次GC周期的标记阶段切到M1视图的话就可以区分了，此时这3个地址视图代表的含义是：

* M1：本次垃圾回收中识别的活跃对象。
* M0：前一次垃圾回收的标记阶段被标记过的活跃对象，对象在转移阶段未被转移，但是在本次垃圾回收中被识别为不活跃对象。
* Remapped：前一次垃圾回收的转移阶段发生转移的对象或者是被应用程序线程访问的对象，但是在本次垃圾回收中被识别为不活跃对象。

### ZGC并发处理演示图

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1654493994032/a15da54676fa4320a134d4d9d6ceb236.png)

**使用地址视图和染色指针有什么好处？**

使用地址视图和染色指针可以加快标记和转移的速度。以前的垃圾回收器通过修改对象头的标记位来标记GC信息，这是有内存存取访问的，而ZGC通过地址视图和染色指针技术，无需任何对象访问，只需要设置地址中对应的标志位即可。这就是ZGC在标记和转移阶段速度更快的原因。

当GC信息不再存储在对象头上时而存在引用指针上时，当确定一个对象已经无用的时候，可以立即重用对应的内存空间，这是把GC信息放到对象头所做不到的。

ZGC只有三个STW阶段：初始标记，再标记，初始转移。

其中，初始标记和初始转移分别都只需要扫描所有GC Roots，其处理时间和GC Roots的数量成正比，一般情况耗时非常短；

再标记阶段STW时间很短，最多1ms，超过1ms则再次进入并发标记阶段。即，ZGC几乎所有暂停都只依赖于GC Roots集合大小，停顿时间不会随着堆的大小或者活跃对象的大小而增加。与ZGC对比，G1的转移阶段完全STW的，且停顿时间随存活对象的大小增加而增加。

### ZGC最佳调优参数：

* `-Xms -Xmx`：堆的最大内存和最小内存，这里都设置为16G，程序的堆内存将保持16G不变。
* `-XX:ReservedCodeCacheSize -XX:InitialCodeCacheSize`：设置CodeCache的大小， JIT编译的代码都放在CodeCache中，一般服务64m或128m就已经足够。我们的服务因为有一定特殊性，所以设置的较大，后面会详细介绍。
* `-XX:+UnlockExperimentalVMOptions -XX:+UseZGC`：启用ZGC的配置。
* `-XX:ConcGCThreads`：并发回收垃圾的线程。默认是总核数的12.5%，8核CPU默认是1。调大后GC变快，但会占用程序运行时的CPU资源，吞吐会受到影响。
* `-XX:ParallelGCThreads`：STW阶段使用线程数，默认是总核数的60%。
* `-XX:ZCollectionInterval`：ZGC发生的最小时间间隔，单位秒。
* `-XX:ZAllocationSpikeTolerance`：ZGC触发自适应算法的修正系数，默认2，数值越大，越早的触发ZGC。
* `-XX:+UnlockDiagnosticVMOptions -XX:-ZProactive`：是否启用主动回收，默认开启，这里的配置表示关闭。
* `-Xlog`：设置GC日志中的内容、格式、位置以及每个日志的大小。

```xml
-Xms16G -Xmx16G 
-XX:ReservedCodeCacheSize=256m -XX:InitialCodeCacheSize=256m 
-XX:+UnlockExperimentalVMOptions -XX:+UseZGC 
-XX:ConcGCThreads=2 -XX:ParallelGCThreads=6 
-XX:ZCollectionInterval=120 -XX:ZAllocationSpikeTolerance=5 
-XX:+UnlockDiagnosticVMOptions -XX:-ZProactive 
-Xlog:safepoint,classhisto*=trace,age*,gc*=info:file=/opt/logs/logs/gc-%t.log:time,tid,tags:filecount=5,filesize=50m 
```

## ZGC垃圾回收触发时机

> 相比于CMS和G1的GC触发机制，ZGC的GC触发机制有很大不同。ZGC的核心特点是并发，GC过程中一直有新的对象产生。如何保证在GC完成之前，新产生的对象不会将堆占满，是ZGC参数调优的第一大目标。因为在ZGC中，当垃圾来不及回收将堆占满时，会导致正在运行的线程停顿，持续时间可能长达秒级之久。

ZGC有多种GC触发机制，总结如下：

* **阻塞内存分配请求触发** ：当垃圾来不及回收，垃圾将堆占满时，会导致部分线程阻塞。我们应当避免出现这种触发方式。日志中关键字是“Allocation Stall”。
* **基于分配速率的自适应算法** ：最主要的GC触发方式，其算法原理可简单描述为”ZGC根据近期的对象分配速率以及GC时间，计算出当内存占用达到什么阈值时触发下一次GC”。自适应算法的详细理论可参考彭成寒《新一代垃圾回收器ZGC设计与实现》一书中的内容。通过ZAllocationSpikeTolerance参数控制阈值大小，该参数默认2，数值越大，越早的触发GC。我们通过调整此参数解决了一些问题。日志中关键字是“Allocation Rate”。
* **基于固定时间间隔** ：通过ZCollectionInterval控制，适合应对突增流量场景。流量平稳变化时，自适应算法可能在堆使用率达到95%以上才触发GC。流量突增时，自适应算法触发的时机可能会过晚，导致部分线程阻塞。我们通过调整此参数解决流量突增场景的问题，比如定时活动、秒杀等场景。日志中关键字是“Timer”。
* **主动触发规则** ：类似于固定间隔规则，但时间间隔不固定，是ZGC自行算出来的时机，我们的服务因为已经加了基于固定时间间隔的触发机制，所以通过-ZProactive参数将该功能关闭，以免GC频繁，影响服务可用性。 日志中关键字是“Proactive”。
* **预热规则** ：服务刚启动时出现，一般不需要关注。日志中关键字是“Warmup”。
* **外部触发** ：代码中显式调用System.gc()触发。 日志中关键字是“System.gc()”。
* **元数据分配触发** ：元数据区不足时导致，一般不需要关注。 日志中关键字是“Metadata GC Threshold”。

### **总结**

内存多重映射和读屏障和染色指针的引入，使 ZGC 的并发性能大幅度提升。

ZGC 只有 3 个需要 STW 的阶段，其中初始标记和初始转移只需要扫描所有 GC Roots，STW 时间 GC Roots 的数量成正比，不会耗费太多时间。再标记过程主要处理并发标记引用地址发生变化的对象，这些对象数量比较少，耗时非常短。可见整个 ZGC 的 STW 时间几乎只跟 GC Roots 数量有关系，不会随着堆大小和对象数量的变化而变化。

ZGC 也有一个缺点，就是浮动垃圾。因为 ZGC 没有分代概念，虽然 ZGC 的 STW 时间在 1ms 以内，但是 ZGC 的整个执行过程耗时还是挺长的。在这个过程中 Java 线程可能会创建大量的新对象，这些对象会成为浮动垃圾，只能等下次 GC 的时候进行回收。 





## 3.2 常用命令

### 3.2.1 jps

> 查看java进程

```
The jps command lists the instrumented Java HotSpot VMs on the target system. The command is limited to reporting information on JVMs for which it has the access permissions.
```

jps主要用来输出JVM中运行的进程状态信息。

### 3.1.1 语法格式

```
jps [options] [hostid]

第一个参数：options

-q 不输出类名、Jar名和传入main方法的参数
-m 输出传入main方法的参数
-l 输出main类或Jar的全限名
-v 输出传入JVM的参数

第二个参数：hostid

主机或者是服务器的id，如果不指定，就默认为当前的主机或者是服务器。
```

### 3.1.2 用例 

![img](https://img.u72.net/20211007/933814a45d8c45448503075d69cdf1ce.jpg)

### 3.2.2 jinfo

> （1）实时查看和调整JVM配置参数

```
The jinfo command prints Java configuration information for a specified Java process or core file or a remote debug server. The configuration information includes Java system properties and Java Virtual Machine (JVM) command-line flags.
```

> （2）查看用法
>
> jinfo -flag name PID     查看某个java进程的name属性的值

```
jinfo -flag MaxHeapSize PID 
jinfo -flag UseG1GC PID
```



> （3）修改
>
> **参数只有被标记为manageable的flags可以被实时修改**

```
jinfo -flag [+|-] PID
jinfo -flag <name>=<value> PID
```

> （4）查看曾经赋过值的一些参数

```
jinfo -flags PID
```

jinfo(Configuration Info for Java)，显示虚拟机配置信息，实时地查看和调整虚拟机各项参数。

#### 3.3.1 语法格式

```
jinfo [option] pid
第一个参数：option
主要选项：
no option 输出全部的参数和系统属性
-flag name 输出对应名称的参数
-flag [+|-]name 开启或者关闭对应名称的参数
-flag name=value 设定对应名称的参数
-flags 输出全部的参数
-sysprops 输出系统属性

第二个参数：pid
指定显示的进程id。
```

#### 3.3.2 用例

- 1) jinfo pid

输出当前 jvm 进程的全部参数和系统属性。

![img](https://img.u72.net/20211007/0fe0431f74d34a968d2021bf155a8268.jpg)

- 2) jinfo -flag name pid

输出对应名称的参数。

![img](https://img.u72.net/20211007/56598dfeac854e4c859a22a455af417d.jpg)

- 3) jinfo -flag [+|-]name pid

开启或者关闭对应名称的参数，使用 jinfo 可以在不重启虚拟机的情况下，可以动态的修改 jvm 的参数。尤其在线上的环境特别有用。

![img](https://img.u72.net/20211007/dbee9335e9814751a234c48d1f47262d.jpg)

- 4) jinfo -flag name=value pid

修改指定参数的值。同示例三，但示例三主要是针对 boolean 值的参数设置的。如果是设置 value值，则需要使用 name=value 的形式。jinfo虽然可以在java程序运行时动态地修改虚拟机参数，但并不是所有的参数都支持动态修改。

![img](https://img.u72.net/20211007/19f710f497bb4d98b1b3d41959026618.jpg)

- 5) jinfo -flags pid

输出全部的参数。

![img](https://img.u72.net/20211007/330b40254abf40c291654e43280dd099.jpg)

- 6) jinfo -sysprops pid

输出当前 jvm 进行的全部的系统属性。

![img](https://img.u72.net/20211007/17f715ae4f4c42629f4906c996ffda11.jpg)

### 3.2.3 jstat



> （1）查看虚拟机性能统计信息

```
The jstat command displays performance statistics for an instrumented Java HotSpot VM. The target JVM is identified by its virtual machine identifier, or vmid option.
```

> （2）查看类装载信息

```
jstat -class PID 1000 10   查看某个java进程的类装载信息，每1000毫秒输出一次，共输出10次
```



> （3）查看垃圾收集信息

```
jstat -gc PID 1000 10
```

jstat监视虚拟机各种运行状态信息，可以显示本地或者是远程虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据。

| **命令选项**      | **涵义**                                                     |
| ----------------- | ------------------------------------------------------------ |
| -class            | 监视类装载、卸载数量、总空间及类装载所耗费的时间             |
| -gc               | 监视java堆状况，包括Eden区、2个survivor区、老年代、永久代等的容量、已用空间、GC时间合计等信息 |
| -gccapacity       | 监视内容与-gc基本相同，但输出主要关注java堆各个区域使用到的最大和最小空间 |
| -gcutil           | 监视内容与-gc基本相同，但输出主要关注已使用空间占总空间的百分比 |
| -gccause          | 与-gcutil功能一样，但是会额外输出导致上一次GC产生的原因      |
| -gcnew            | 监视新生代GC的情况                                           |
| -gcnewcapacity    | 监视内容与-gcnew基本相同，输出主要关注使用到的最大和最小空间 |
| -gcold            | 监视老年代GC的情况                                           |
| -gcoldcapacity    | 监视内容与-gcold基本相同，输出主要关注使用到的最大和最小空间 |
| -gcpermcapacity   | 输出永久代使用到的最大和最小空间（java8之前版本）            |
| -gcmetacapacity   | 元数据空间统计（java8及之后版本）                            |
| -compiler         | 输出JIT编译期编译过的方法、耗时等信息                        |
| -printcompilation | 输出已经被JIT编译的方法                                      |

#### 3.2.1 语法格式

```
jstat [ generalOption | outputOptions vmid [ interval[s|ms] [ count ] ]

jstat [-命令选项] [vmid] [间隔时间/毫秒] [查询次数]
 
jstat -class 3176 1000 10（每隔1s，共输出10次）

第一个参数：generalOption | outputOptions
这个参数表示的option，代表着用户希望查询的虚拟机信息，分为类加载、垃圾收集、运行期编译状况3类。

第二个参数：vmid
vmid是Java虚拟机ID，即当前运行的java进程号，在Linux/Unix系统上一般就是进程ID。

第三个参数：interval
interval是采样时间间隔，单位为秒或毫秒。

第四个参数：count
count表示的是采样数，即打印次数，如果缺省则打印无数次。
```

#### 3.2.2 用例

- 1）-class 查看类加载信息

![img](https://img.u72.net/20211007/749fb7c8981d44fab8be14d22a382ab8.jpg)

```
Loaded:加载class的数量
Bytes：所占用空间大小
Unloaded：未加载数量
Bytes:未加载占用空间
Time：时间
```

- 2） -gcutil 总结垃圾回收统计

![img](https://img.u72.net/20211007/db6fbcf4c4bd49c59bffcf0fca419454.jpg)

```
S0: 新生代中Survivor space 0区（幸存1区）已使用空间的百分比
S1: 新生代中Survivor space 1区（幸存2区）已使用空间的百分比
E: 新生代（伊甸园区）已使用空间的百分比
O: 老年代已使用空间的百分比
M：元数据区使用比例
CCS：压缩使用比例
YGC: 从应用程序启动到当前，发生Yang GC 的次数（年轻代垃圾回收次数）
YGCT: 从应用程序启动到当前，Yang GC所用的时间【单位秒】
FGC: 从应用程序启动到当前，发生Full GC的次数（老年代垃圾回收次数）
FGCT: 从应用程序启动到当前，Full GC所用的时间
GCT: 从应用程序启动到当前，用于垃圾回收的总时间【单位秒】
```

-  3） -gc 查看垃圾回收情况

![img](https://img.u72.net/20211007/890ec15a11154ce3905e7ee39bf7425c.jpg)

```
S0C：第一个幸存区的大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
OC：老年代大小
OU：老年代使用大小
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
```

- 4） -gccause 查看垃圾回收情况

![img](https://img.u72.net/20211007/e56bffbe48d84d378f920615986ecf43.jpg)

```
S0 — Heap上的 Survivor space 0 区已使用空间的百分比     
S1 — Heap上的 Survivor space 1 区已使用空间的百分比     
E   — Heap上的 Eden space 区已使用空间的百分比    
O   — Heap上的 Old space 区已使用空间的百分比     
P   — Perm space 区已使用空间的百分比 
YGC — 从应用程序启动到采样时发生 Young GC 的次数 
YGCT– 从应用程序启动到采样时 Young GC 所用的时间(单位秒)     
FGC — 从应用程序启动到采样时发生 Full GC 的次数 
FGCT– 从应用程序启动到采样时 Full GC 所用的时间(单位秒)  
GCT — 从应用程序启动到采样时用于垃圾回收的总时间(单位秒)   
```

- 5） -gcnew 新生代垃圾回收统计

![img](https://img.u72.net/20211007/3ef007fd6df14c498a8327304be8d6d4.jpg)

```
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
S0U：第一个幸存区的使用大小
S1U：第二个幸存区的使用大小
TT:对象在新生代存活的次数
MTT:对象在新生代存活的最大次数
DSS:期望的幸存区大小
EC：伊甸园区的大小
EU：伊甸园区的使用大小
YGC：年轻代垃圾回收次数
YGCT：年轻代垃圾回收消耗时间
```

- 6） -gcold 老年代垃圾回收统计

![img](https://img.u72.net/20211007/d86e10fa0ec84f44a389d561cd4bd27b.jpg)

```
MC：方法区大小
MU：方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
OC：老年代大小
OU：老年代使用大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
```

- 7）-compiler 查看JIT编译信息

![img](https://img.u72.net/20211007/d8da0c6f040f490bb155e5026991c7f9.jpg)

```
Compiled：编译数量。
Failed：失败数量
Invalid：不可用数量
Time：时间
FailedType：失败类型
FailedMethod：失败的方法
```

- 8） -gccapacity 堆内存统计

![img](https://img.u72.net/20211007/c42182de42764345bdabc7026f6cdbc8.jpg)

```
NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0C：第一个幸存区大小
S1C：第二个幸存区的大小
EC：伊甸园区的大小
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC:当前老年代大小
MCMN:最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代gc次数
FGC：老年代GC次数
```

- 9）-gcnewcapacity 新生代内存统计

![img](https://img.u72.net/20211007/8b115163947e493895246337203bca06.jpg)

```
NGCMN：新生代最小容量
NGCMX：新生代最大容量
NGC：当前新生代容量
S0CMX：最大幸存1区大小
S0C：当前幸存1区大小
S1CMX：最大幸存2区大小
S1C：当前幸存2区大小
ECMX：最大伊甸园区大小
EC：当前伊甸园区大小
YGC：年轻代垃圾回收次数
FGC：老年代回收次数
```

- 10）-gcoldcapacity 老年代内存统计

![img](https://img.u72.net/20211007/684421dc25ab45d5bdef94b72c47bd36.jpg)

```
OGCMN：老年代最小容量
OGCMX：老年代最大容量
OGC：当前老年代大小
OC：老年代大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
```

- 11）-gcmetacapacity 元数据空间统计

![img](https://img.u72.net/20211007/0de79b07f8d04ab4a7f4ce64a90ade1b.jpg)

```
MCMN: 最小元数据容量
MCMX：最大元数据容量
MC：当前元数据空间大小
CCSMN：最小压缩类空间大小
CCSMX：最大压缩类空间大小
CCSC：当前压缩类空间大小
YGC：年轻代垃圾回收次数
FGC：老年代垃圾回收次数
FGCT：老年代垃圾回收消耗时间
GCT：垃圾回收消耗总时间
```

- 12）-printcompilation JVM编译方法统计

![img](https://img.u72.net/20211007/c7e6401974394455b20eb03cd8e64f4b.jpg)

```
Compiled：最近编译方法的数量
Size：最近编译方法的字节码数量
Type：最近编译方法的编译类型。
Method：方法名标识。
```

#### 3.2.3 jstat的局限性

没有提供有关GC活动的丰富详细信息。 例如，从jstat中您将不知道：

- 如果一次样本中报告了多个GC事件，则我们将不知道每个GC事件的暂停时间是多少。
- 用户（即Java层），系统（即内核）和用户花费了多少时间。
- 有多少个GC线程正在工作，并占用了多少时间？
- 一个GC事件具有几个子阶段（例如初始标记，清理，备注，并发标记……）。 无法提供信息分类。
- 每个GC事件回收多少字节。
- 有时，jstat报告的数据也会产生误导 。

如果您想进行准确的GC分析，

### 3.2.4 jstack

> （1）查看线程堆栈信息

```
The jstack command prints Java stack traces of Java threads for a specified Java process, core file, or remote debug server.
```

> （2）用法

```
jps 找到对应线程PID
jstack PID
```



显示虚拟机的线程快照

jstack(Stack Trace for Java) ，主要用于生成java虚拟机当前时刻的线程快照。线程快照是当前java虚拟机内每一条线程正在执行的方法堆栈的集合，生成线程快照的主要目的是定位线程出现长时间停顿的原因，如线程间死锁、死循环、请求外部资源导致的长时间等待等。 线程出现停顿的时候通过jstack来查看各个线程的调用堆栈，就可以知道没有响应的线程到底在后台做什么事情，或者等待什么资源。

另外，jstack工具还可以附属到正在运行的java程序中，看到当时运行的java程序的java stack和native stack的信息, 如果现在运行的java程序呈现hung的状态，jstack是非常有用的。

#### 3.6.1 语法格式

```
$jstack [ option ] pid
$jstack [ option ] executable core
$jstack [ option ] [server-id@]remote-hostname-or-IP

参数说明:
pid: java应用程序的进程号,一般可以通过jps来获得;
executable:产生core dump的java可执行程序;
core:打印出的core文件;
remote-hostname-or-ip:远程debug服务器的名称或IP;
server-id: 唯一id,假如一台主机上多个远程debug服务;
```

#### 3.6.2 用例

- 查看输出 jstack –l pid

通过jstack输出的线程信息主要包括：jvm自身线程、用户线程等。其中jvm线程会在jvm启动时就会存在。对于用户线程则是在用户访问时才会生成。

1）jvm线程：

在线程中，有一些 JVM内部的后台线程，来执行譬如垃圾回收，或者低内存的检测等等任务，这些线程往往在JVM初始化的时候就存在，如下所示：

```java
"Attach Listener" daemon prio=10 tid=0x0000000052fb8000 nid=0xb8f waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE



   Locked ownable synchronizers:

        - None

destroyJavaVM" prio=10 tid=0x00002aaac1225800 nid=0x7208 waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE



   Locked ownable synchronizers:

        - None
```

2）用户级别的线程

还有一类线程是用户级别的，它会根据用户请求的不同而发生变化。该类线程的运行情况往往是我们所关注的重点。而且这一部分也是最容易产生死锁的地方。

```java
"qtp496432309-42" prio=10 tid=0x00002aaaba2a1800 nid=0x7580 waiting on condition [0x00000000425e9000]

   java.lang.Thread.State: TIMED_WAITING (parking)

        at sun.misc.Unsafe.park(Native Method)

        - parking to wait for  <0x0000000788cfb020> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)

        at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:198)

        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.awaitNanos(AbstractQueuedSynchronizer.java:2025)

        at org.eclipse.jetty.util.BlockingArrayQueue.poll(BlockingArrayQueue.java:320)

        at org.eclipse.jetty.util.thread.QueuedThreadPool$2.run(QueuedThreadPool.java:479)

        at java.lang.Thread.run(Thread.java:662)



   Locked ownable synchronizers:

        - None
```

从上述的代码示例中我们可以看到该用户线程的以下几类信息：

Ø 线程的状态：waiting on condition(等待条件发生)

Ø 线程的调用情况；

Ø 线程对资源的锁定情况：Locked 

- jstack检测死锁

1）死锁代码

```java
public class DeadLock 
    private static Object objA = new Object();
    private static Object objB = new Object();

    public static void main(String[] args) 
        Thread thread1 = new Thread(new Thread1());
        Thread thread2 = new Thread(new Thread2());
        thread1.start();
        thread2.start();
    

    private static class Thread1 implements Runnable
        @Override
        public void run() 
            synchronized (objA) 
                System.out.println("线程1得到A对象的锁");
                try 
                    Thread.sleep(3000);
                 catch (InterruptedException e) 
                    e.printStackTrace();
                

                synchronized (objB) 
                    System.out.println("线程1得到B对象的锁");
                
            
        
    

    private static class Thread2 implements Runnable
        @Override
        public void run() 
            synchronized (objB) 
                System.out.println("线程2得到B对象的锁");
                try 
                    Thread.sleep(3000);
                 catch (InterruptedException e) 
                    e.printStackTrace();
                

                synchronized (objA) 
                    System.out.println("线程2得到A对象的锁");
                
            
          
    
```

2）运行结果只能看到两个线程各只拿到了一个锁，在一直等待对方的锁释放。

```
线程1得到A对象的锁
线程2得到B对象的锁
```

3）使用 jps 来查看对应的 PID ，然后使用 jstack 来查看其线程情况：

```java
C:\\Users\\server>jps
4240 Jps
480 DeadLock
C:\\Users\\server>jstack 480

Full thread dump Java HotSpot(TM) 64-Bit Server VM (24.45-b08 mixed mode):

"DestroyJavaVM" prio=6 tid=0x00000000047c1000 nid=0x9878 waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"Thread-1" prio=6 tid=0x0000000010aa3000 nid=0xafa0 waiting for monitor entry [0x000000001105f000]

   java.lang.Thread.State: BLOCKED (on object monitor)

        at com.zaimeibian.Test$Thread2.run(Test.java:46)

        - waiting to lock <0x00000007c099cc20> (a java.lang.Object)

        - locked <0x00000007c099cc30> (a java.lang.Object)

        at java.lang.Thread.run(Thread.java:744)

"Thread-0" prio=6 tid=0x0000000010aa2800 nid=0xae74 waiting for monitor entry [0x0000000010f5f000]

   java.lang.Thread.State: BLOCKED (on object monitor)

        at com.zaimeibian.Test$Thread1.run(Test.java:27)

        - waiting to lock <0x00000007c099cc30> (a java.lang.Object)

        - locked <0x00000007c099cc20> (a java.lang.Object)

        at java.lang.Thread.run(Thread.java:744)

"Service Thread" daemon prio=6 tid=0x000000000f10a000 nid=0x9a8c runnable [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" daemon prio=10 tid=0x000000000f109800 nid=0xaf28 waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" daemon prio=10 tid=0x000000000f105800 nid=0x85dc waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"Attach Listener" daemon prio=10 tid=0x000000000f104800 nid=0xac04 waiting on condition [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"Signal Dispatcher" daemon prio=10 tid=0x000000000f102000 nid=0xa678 runnable [0x0000000000000000]

   java.lang.Thread.State: RUNNABLE

"Finalizer" daemon prio=8 tid=0x000000000f0bd000 nid=0xaed8 in Object.wait() [0x000000001045f000]

   java.lang.Thread.State: WAITING (on object monitor)

        at java.lang.Object.wait(Native Method)

        - waiting on <0x00000007c0905568> (a java.lang.ref.ReferenceQueue$Lock)

        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:135)

        - locked <0x00000007c0905568> (a java.lang.ref.ReferenceQueue$Lock)

        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:151)

        at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:189)

"Reference Handler" daemon prio=10 tid=0x000000000f0b2000 nid=0xaedc in Object.wait() [0x000000001035f000]

   java.lang.Thread.State: WAITING (on object monitor)

        at java.lang.Object.wait(Native Method)

        - waiting on <0x00000007c09050f0> (a java.lang.ref.Reference$Lock)

        at java.lang.Object.wait(Object.java:503)

        at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:133)

        - locked <0x00000007c09050f0> (a java.lang.ref.Reference$Lock)

"VM Thread" prio=10 tid=0x000000000f0b0000 nid=0xaef0 runnable

"GC task thread#0 (ParallelGC)" prio=6 tid=0x00000000047d6000 nid=0xacb0 runnable

"GC task thread#1 (ParallelGC)" prio=6 tid=0x00000000047d8000 nid=0xaee0 runnable

"GC task thread#2 (ParallelGC)" prio=6 tid=0x00000000047d9800 nid=0xaed4 runnable

"GC task thread#3 (ParallelGC)" prio=6 tid=0x00000000047db000 nid=0xac54 runnable

"VM Periodic Task Thread" prio=10 tid=0x000000000f132000 nid=0xaff0 waiting on condition

JNI global references: 105

Found one Java-level deadlock:

=============================

"Thread-1":

  waiting to lock monitor 0x000000000f0ba488 (object 0x00000007c099cc20, a java.lang.Object),

  which is held by "Thread-0"

"Thread-0":

  waiting to lock monitor 0x000000000f0bcf28 (object 0x00000007c099cc30, a java.lang.Object),

  which is held by "Thread-1"

Java stack information for the threads listed above:

===================================================

"Thread-1":

        at com.zaimeibian.Test$Thread2.run(Test.java:46)

        - waiting to lock <0x00000007c099cc20> (a java.lang.Object)

        - locked <0x00000007c099cc30> (a java.lang.Object)

        at java.lang.Thread.run(Thread.java:744)

"Thread-0":

        at com.zaimeibian.Test$Thread1.run(Test.java:27)

        - waiting to lock <0x00000007c099cc30> (a java.lang.Object)

        - locked <0x00000007c099cc20> (a java.lang.Object)

        at java.lang.Thread.run(Thread.java:744)

Found 1 deadlock.
```

 可以看到 jstack 打印出了线程的状态，而且发现一个死锁。另外，线程状态有以下几种：
\- RUNNABLE 线程运行中或 I/O 等待
\- BLOCKED 线程在等待 monitor 锁( synchronized 关键字)
\- TIMED_WAITING 线程在等待唤醒，但设置了时限
\- WAITING 线程在无限等待唤醒

### 3.2.5 jmap

![image-20240120210039997](E:\图灵课堂\JVM\JVM专题.assets\image-20240120210039997.png)

> （1）生成堆转储快照

```
The jmap command prints shared object memory maps or heap memory details of a specified process, core file, or remote debug server.
```

> （2）打印出堆内存相关信息

```
jmap -heap PID
```

```
jinfo -flag UsePSAdaptiveSurvivorSizePolicy 35352
-XX:SurvivorRatio=8
```



> （3）dump出堆内存相关信息

```
jmap -dump:format=b,file=heap.hprof PID
```



> （4）要是在发生堆内存溢出的时候，能自动dump出该文件就好了

一般在开发中，JVM参数可以加上下面两句，这样内存溢出时，会自动dump出该文件

-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heap.hprof

```
设置堆内存大小: -Xms20M -Xmx20M
启动，然后访问localhost:9090/heap，使得堆内存溢出
```

jmap(Memory Map for Java)，内存映像工具，用于生成堆转存的快照，一般是heapdump或者dump文件。如果不使用jmap命令，可以使用-XX:+HeapDumpOnOutOfMemoryError参数，当虚拟机发生内存溢出的时候可以产生快照。或者使用kill -3 pid也可以产生。jmap的作用并不仅仅是为了获取dump文件，也可以查看堆内对象示例的统计信息、查看 ClassLoader 的信息以及 finalizer 队列，java堆和永久代的详细信息，如空间使用率，当前用的哪种收集器。

#### 3.4.1 语法格式

```
jmap [option] vmid

option： 选项参数。
pid： 需要打印配置信息的进程ID。
executable： 产生核心dump的Java可执行文件。
core： 需要打印配置信息的核心文件。
server-id 可选的唯一id，如果相同的远程主机上运行了多台调试服务器，用此选项参数标识服务器。
remote server IP or hostname 远程调试服务器的IP地址或主机名。

option
no option： 查看进程的内存映像信息,类似 Solaris pmap 命令。
heap： 显示Java堆详细信息，如使用哪种回收器、参数配置、分代状况等，只在Linux/Solaris平台下有效。
histo[:live]： 显示堆中对象的统计信息，包括类、实例数量、合计容量。
clstats：打印类加载器信息
finalizerinfo： 显示在F-Queue队列等待Finalizer线程执行finalizer方法的对象，只在Linux/Solaris平台下有效。
dump:<dump-options>：生成java堆转储快照，格式为-dump:[live, ]format=b, file=<filename>, 其中live子参数说明是否只dump出存活的对象。
permstat: 以ClassLoader为统计口径显示永久代内存状态，只在Linux/Solaris平台下有效。
F： 当虚拟机进程对-dump没有响应时，可使用这个选项强制生成dump快照。使用-dump或者-histo参数. 在这个模式下,live子参数无效.
help：打印帮助信息
J<flag>：指定传递给运行jmap的JVM的参数
```

#### 3.4.2 用例

![img](https://img.u72.net/20211007/eae313a3567b4883be5c1f285af8d53e.jpg)

- 1） jmap pid

查看进程的内存映像信息,类似 Solaris pmap 命令。使用不带选项参数的jmap打印共享对象映射，将会打印目标虚拟机中加载的每个共享对象的起始地址、映射大小以及共享对象文件的路径全称。这与Solaris的pmap工具比较相似。

![img](https://img.u72.net/20211007/6bb194a580e54cca968104e1f8430d2e.jpg)

-  2） jmap -heap pid

显示Java堆详细信息。打印一个堆的摘要信息，包括使用的GC算法、堆配置信息和各内存区域内存使用信息。

![img](https://img.u72.net/20211007/32aa0c7f9c1d4479be9f66d62527875b.jpg)



```
C:\\Users\\server>jmap -heap 14268
Attaching to process ID 14268, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.111-b14

using thread-local object allocation.
Parallel GC with 8 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 5368709120 (5120.0MB)
   NewSize                  = 2147483648 (2048.0MB)
   MaxNewSize               = 2147483648 (2048.0MB)
   OldSize                  = 3221225472 (3072.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 524288000 (500.0MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 792723456 (756.0MB)
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 2145386496 (2046.0MB)
   used     = 1743475808 (1662.7080993652344MB)
   free     = 401910688 (383.2919006347656MB)
   81.26628051638487% used
From Space:
   capacity = 1048576 (1.0MB)
   used     = 786432 (0.75MB)
   free     = 262144 (0.25MB)
   75.0% used
To Space:
   capacity = 1048576 (1.0MB)
   used     = 0 (0.0MB)
   free     = 1048576 (1.0MB)
   0.0% used
PS Old Generation
   capacity = 3221225472 (3072.0MB)
   used     = 416272888 (396.98876190185547MB)
   free     = 2804952584 (2675.0112380981445MB)
   12.922811259826025% used

31993 interned Strings occupying 3071760 bytes.
```

- 3） jmap -histo:live pid

显示堆中对象的统计信息。其中包括每个Java类、对象数量、内存大小(单位：字节)、完全限定的类名。打印的虚拟机内部的类名称将会带有一个’*’前缀。如果指定了live子选项，则只计算活动的对象。

采用jmap -histo pid>a.log日志将其保存，在一段时间后，使用文本对比工具，可以对比出GC回收了哪些对象。

jmap -dump:format=b,file=outfile 3024可以将3024进程的内存heap输出出来到outfile文件里，再配合MAT（内存分析工具）。

![img](https://img.u72.net/20211007/da1b532b0b7240b8b60a5e201db9088a.jpg)

class name是对象类型，说明如下：

```
B  byte
C  char
D  double
F  float
I  int
J  long
Z  boolean
[  数组，如[I表示int[]
[L+类名 其他对象 
```

- 4） jmap -clstats pid

打印类加载器信息。-clstats是-permstat的替代方案，在JDK8之前，-permstat用来打印类加载器的数据，打印Java堆内存的永久保存区域的类加载器的智能统计信息。对于每个类加载器而言，它的名称、活跃度、地址、父类加载器、它所加载的类的数量和大小都会被打印。此外，包含的字符串数量和大小也会被打印。

![img](https://img.u72.net/20211007/4c3fcd1f20e344f68b7ca8d6e31513e9.jpg)

- 5） jmap -finalizerinfo pid

打印等待终结的对象信息。Number of objects pending for finalization: 0 说明当前F-QUEUE队列中并没有等待Fializer线程执行final。

![img](https://img.u72.net/20211007/5b15c46ed07344a69662f5662dd2cc56.jpg)

6） jmap -dump:format=b,file=heapdump.phrof pid

生成堆转储快照dump文件。以hprof二进制格式转储Java堆到指定filename的文件中。live子选项是可选的。如果指定了live子选项，堆中只有活动的对象会被转储。想要浏览heap dump，你可以使用jhat(Java堆分析工具)读取生成的文件。

这个命令执行，JVM会将整个heap的信息dump写入到一个文件，heap如果比较大的话，就会导致这个过程比较耗时，并且执行的过程中为了保证dump的信息是可靠的，所以会暂停应用， 线上系统慎用。

![img](https://img.u72.net/20211007/93fbe5a77dfc40f9b97d22c8b9b5de8a.jpg)

### 3.2.6 jhat

分析内存存储快照

jhat(JVM Heap Dump Browser/Java Heap Analysis Tool) ，是java虚拟机自带的一种虚拟机堆转储快照分析工具，**不推荐使用**，消耗资源而且慢。

可以用jhat命令将dump出来的hprof文件转成html的形式，然后通过http访问可以查看堆情况。

通过浏览器访问http://localhost:7000则可以看到如下信息，显示jvm中所有非平台类信息。通过这些连接可以进一步查看所有类信息（包括JAVA平台的类）、所有类的实例数量以及实例的基本信息。最后，还有一个连接指向OQL查询页面。

#### 3.5.1 语法格式

```
jhat C:\\Users\\server\\heapdump.phrof
phrof 文件路径
```

#### 3.5.2 用例

![img](https://img.u72.net/20211007/e981fa7ca64549aba3c6498cefe2a4fe.jpg)

![img](https://img.u72.net/20211007/2a7adb1de2474c088abc4a027ceafc09.jpg)

这样就启动起来了一个简易的HTTP服务，端口号是7000，尝试一下用浏览器访问一下它，本地的可以通过http://localhost:7000，就可以得到这样的页面：

![img](https://img.u72.net/20211007/b198b9642d35408fa1785fe3115fa938.jpg)

All classes including platform 把所有类信息显示出来（默认是不包括Java平台的类）

查看堆异常主要关注两个：

Show instance counts for all classes (excluding platform) 平台外的所有对象信息
Show heap histogram 显示堆的统计信息

![img](https://img.u72.net/20211007/a61932cb62b74ea2ad189a91a6d2f13c.jpg)

![img](https://img.u72.net/20211007/6af2617ea07c41d68da26ae964cce4c2.jpg)

 ![img](https://img.u72.net/20211007/b858908392ee411eae31146341fdd882.jpg)

### 

# 内存溢出

####  **一、基本概念**

内存溢出：简单地说内存溢出就是指程序运行过程中申请的内存大于系统能够提供的内存，导致无法申请到足够的内存，于是就发生了内存溢出。

内存泄漏：内存泄漏指程序运行过程中分配内存给临时变量，用完之后却没有被GC回收，始终占用着内存，既不能被使用也不能分配给其他程序，于是就发生了内存泄漏。 

内存溢出 out of memory，是指程序在申请内存时，没有足够的内存空间供其使用，出现out of memory;

内存泄露 memory leak，是指程序在申请内存后，无法释放已申请的内存空间，一次内存泄露危害可以忽略，但内存泄露堆积后果很严重，无论多少内存，迟早会被占光。

memory leak会最终会导致out of memory!

内存泄露是指无用对象(不再使用的对象)持续占有内存或无用对象的内存得不到及时释放，从而造成的内存空间的浪费称为内存泄露。内存泄露有时不严重且不易察觉，这样开发者就不知道存在内存泄露，但有时也会很严重，会提示你Out of memory。 

#### **二、内存溢出的常见情况**

内存溢出有以下几种常见的情况：

**1、java.lang.OutOfMemoryError: PermGen space (持久带溢出)**

我们知道jvm通过持久带实现了java虚拟机规范中的方法区，而运行时常量池就是保存在方法区中的，因此发生这种溢出可能是运行时常量池溢出，或是由于程序中使用了大量的jar或class，使得方法区中保存的class对象没有被及时回收或者class信息占用的内存超过了配置的大小。

**2、java.lang.OutOfMemoryError: Java heap space (堆溢出)**

发生这种溢出的原因一般是创建的对象太多，在进行垃圾回收之前对象数量达到了最大堆的容量限制。

解决这个区域异常的方法一般是通过内存映像分析工具对Dump出来的堆转储快照进行分析，看到底是内存溢出还是内存泄漏。如果是内存泄漏，可进一步通过工具查看泄漏对象到GC Roots的引用链，定位出泄漏代码的位置，修改程序或算法;如果不存在泄漏，就是说内存中的对象确实都还必须存活，那就应该检查虚拟机的堆参数-Xmx(最大堆大小)和-Xms(初始堆大小)，与机器物理内存对比看是否可以调大。

**3、虚拟机栈和本地方法栈溢出**

如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError。

如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError。 

#### **三、内存泄漏**

内存泄漏的根本原因是长生命周期的对象持有短生命周期对象的引用，尽管短生命周期的对象已经不再需要，但由于长生命周期对象持有它的引用而导致不能被回收。

以发生的方式来分类，内存泄漏可以分为4类：

1、常发性内存泄漏。发生内存泄漏的代码会被多次执行到，每次被执行的时候都会导致一块内存泄漏。

2、偶发性内存泄漏。发生内存泄漏的代码只有在某些特定环境或操作过程下才会发生。常发性和偶发性是相对的。对于特定的环境，偶发性的也许就变成了常发性的。所以测试环境和测试方法对检测内存泄漏至关重要。

3、一次性内存泄漏。发生内存泄漏的代码只会被执行一次，或者由于算法上的缺陷，导致总会有一块仅且一块内存发生泄漏。比如，在类的构造函数中分配内存，在析构函数中却没有释放该内存，所以内存泄漏只会发生一次。

4、隐式内存泄漏。程序在运行过程中不停的分配内存，但是直到结束的时候才释放内存。严格的说这里并没有发生内存泄漏，因为最终程序释放了所有申请的内存。但是对于一个服务器程序，需要运行几天，几周甚至几个月，不及时释放内存也可能导致最终耗尽系统的所有内存。所以，我们称这类内存泄漏为隐式内存泄漏。

从用户使用程序的角度来看，内存泄漏本身不会产生什么危害，作为一般的用户，根本感觉不到内存泄漏的存在。真正有危害的是内存泄漏的堆积，这会最终消耗尽系统所有的内存。从这个角度来说，一次性内存泄漏并没有什么危害，因为它不会堆积，而隐式内存泄漏危害性则非常大，因为较之于常发性和偶发性内存泄漏它更难被检测到。 

**下面总结几种常见的内存泄漏：**

**1、静态集合类引起的内存泄漏：**

像HashMap、Vector等的使用最容易出现内存泄露，这些静态变量的生命周期和应用程序一致，他们所引用的所有的对象Object也不能被释放，从而造成内存泄漏，因为他们也将一直被Vector等引用着。

复制

```java
Vector<Object> v=new Vector<Object>(100); 
for (int i = 1; i<100; i++) { 
    Object o = new Object(); 
    v.add(o); 
    o = null; 
} 

```

在这个例子中，循环申请Object 对象，并将所申请的对象放入一个Vector 中，如果仅仅释放引用本身(o=null)，那么Vector 仍然引用该对象，所以这个对象对GC 来说是不可回收的。因此，如果对象加入到Vector 后，还必须从Vector 中删除，最简单的方法就是将Vector对象设置为null。

**2、修改HashSet中对象的参数值，且参数是计算哈希值的字段**

当一个对象被存储到HashSet集合中以后，修改了这个对象中那些参与计算哈希值的字段后，这个对象的哈希值与最初存储在集合中的就不同了，这种情况下，用contains方法在集合中检索对象是找不到的，这将会导致无法从HashSet中删除当前对象，造成内存泄漏，举例如下：

复制

```java
public static void main(String[] args){ 
 Set<Person> set = new HashSet<Person>(); 
 Person p1 = new Person("张三","1",25); 
 Person p2 = new Person("李四","2",26); 
 Person p3 = new Person("王五","3",27); 
 set.add(p1); 
 set.add(p2); 
 set.add(p3); 
 System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:3 个元素! 
 p3.setAge(2); //修改p3的年龄,此时p3元素对应的hashcode值发生改变 
 set.remove(p3); //此时remove不掉，造成内存泄漏 
 set.add(p3); //重新添加，可以添加成功 
 System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:4 个元素! 
  
 for (Person person : set){ 
 System.out.println(person); 
 } 
} 
```

**3、监听器**

在java 编程中，我们都需要和监听器打交道，通常一个应用当中会用到很多监听器，我们会调用一个控件的诸如addXXXListener()等方法来增加监听器，但往往在释放对象的时候却没有记住去删除这些监听器，从而增加了内存泄漏的机会。

**4、各种连接**

比如数据库连接(dataSourse.getConnection())，网络连接(socket)和io连接，除非其显式的调用了其close() 方法将其连接关闭，否则是不会自动被GC 回收的。对于Resultset 和Statement 对象可以不进行显式回收，但Connection 一定要显式回收，因为Connection 在任何时候都无法自动回收，而Connection一旦回收，Resultset 和Statement 对象就会立即为NULL。但是如果使用连接池，情况就不一样了，除了要显式地关闭连接，还必须显式地关闭Resultset Statement 对象(关闭其中一个，另外一个也会关闭)，否则就会造成大量的Statement 对象无法释放，从而引起内存泄漏。这种情况下一般都会在try里面去连接，在finally里面释放连接。

**5、单例模式**

如果单例对象持有外部对象的引用，那么这个外部对象将不能被jvm正常回收，导致内存泄露。

不正确使用单例模式是引起内存泄露的一个常见问题，单例对象在被初始化后将在JVM的整个生命周期中存在(以静态变量的方式)，如果单例对象持有外部对象的引用，那么这个外部对象将不能被jvm正常回收，导致内存泄露，考虑下面的例子：

复制

```java
lass A{ 
 public A(){ 
 B.getInstance().setA(this); 
 } 
 .... 
} 
//B类采用单例模式 
class B{ 
 private A a; 
 private static B instance=new B(); 
 public B(){} 
  
 public static B getInstance(){ 
 return instance; 
 } 
  
 public void setA(A a){ 
 this.a=a; 
 } 
 //getter... 
} 

```

显然B采用singleton模式，它持有一个A对象的引用，而这个A类的对象将不能被回收。想象下如果A是个比较复杂的对象或者集合类型会发生什么情况。 

### 避免内存泄漏的几点建议：

1、尽早释放无用对象的引用。

2、避免在循环中创建对象。

3、使用字符串处理时避免使用String，应使用StringBuffer。

4、尽量少使用静态变量，因为静态变量存放在永久代，基本不参与垃圾回收。



### 内存泄露和内存溢出的区别

内存泄露是指程序在运行过程中分配的内存无法被正常释放，而内存溢出通常是由于程序需要的内存超过了可用的内存限制，或者递归调用导致栈空间耗尽，或者内存泄漏导致的，而内存泄漏则是由于程序中存在未释放的动态分配内存、对象引用未被正确释放或循环引用导致的。

内存溢出和内存泄漏是两个与内存管理相关的概念，它们都可能导致程序运行时的问题，但是它们的原因和表现方式有所不同。下面将详细解释内存溢出和内存泄漏的区别。



一：内存溢出（Memory Overflow）是指程序在申请内存时，无法获得所需的内存空间，导致程序中断或崩溃。内存溢出通常发生在以下几种情况下：

分配的内存超过了操作系统或应用程序所能提供的限制。例如，32位操作系统的进程最大可用内存为4GB，如果程序请求分配超过这个限制的内存，就会发生内存溢出。

递归调用导致的内存溢出。在递归函数中，每一次调用都会在内存中创建一个函数调用栈帧，如果递归调用的次数过多，就会耗尽可用的栈空间，导致内存溢出。

内存泄漏导致的内存溢出。当程序分配了一块内存空间，但在使用完毕后没有正确释放，这块内存就无法重新使用，最终导致内存溢出。

二：内存泄漏（Memory Leak）是指程序在运行过程中，分配的内存空间无法被正常释放，导致内存的使用量不断增加，最终耗尽可用的内存。内存泄漏通常发生在以下几种情况下：

程序中存在未释放的动态分配内存。例如，程序使用malloc或new关键字分配了一块内存空间，但在使用完毕后没有调用free或delete来释放内存，这就造成了内存泄漏。

对象引用未被正确释放。当一个对象在程序中没有被正确释放时，该对象所占用的内存空间就会一直存在，从而导致内存泄漏。

循环引用导致的内存泄漏。当两个或多个对象之间相互引用，且没有外部引用指向它们时，这些对象就会形成一个循环引用，导致它们无法被垃圾回收器正常释放，进而引发内存泄漏。

内存溢出和内存泄漏的区别在于，内存溢出是指程序在申请内存时无法获得所需的内存空间，而内存泄漏是指程序在运行过程中分配的内存无法被正常释放。内存溢出通常是由于程序需要的内存超过了可用的内存限制，或者递归调用导致栈空间耗尽，或者内存泄漏导致的。而内存泄漏则是由于程序中存在未释放的动态分配内存、对象引用未被正确释放或循环引用导致的。

为了避免内存溢出和内存泄漏，程序员需要注意合理使用内存资源，及时释放不再使用的内存。使用合适的数据结构和算法，正确使用动态内存分配函数，以及避免循环引用等问题都是预防内存溢出和内存泄漏的重要手段。此外，使用内存管理工具和调试器可以帮助程序员及时发现和解决内存问题。

# JVM工具

## **性能监控工具**

### jconsole

**JConsole工具是JDK自带的图形化性能监控工具。并通过JConsole工具， 可以查看Java应用程序的运行概况， 监控堆信息、 元空间使用情况及类的加载情况等。**

JConsole程序在%JAVA_HOM E%/bin目录下

或者你可以直接在命令行对他进行打印

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/12597031b6e44327b0c12ac06244244a.png)

会显示如下界面：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/889f8f9b80354e588892b3eb3ae87c1c.png)

#### JConsole的连接方式

JConsole分为本地连接以及远程连接，一般我们本地连接在小型单体项目中用于本地分析较多，大型项目以及线上生产环境一般采用远程连接的方式。

##### 本地连接：

Jconsole会在本地自动寻找当前的可监控进程，所以我们可以只要本地启动项目，就可以自动匹配并点击进去。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/242f9982bf274d099b25df1c28963b90.png)

##### 远程连接：

1. 设置被监控的Java虚拟机启动的參数，一般的情况下，会有下面三个參数，各自是：

-Dcom.sun.management.jmxremote.port=1090

-Dcom.sun.management.jmxremote.ssl=false

-Dcom.sun.management.jmxremote.authenticate=false

也就是说，你需要在启动参数后面加上这几个参数

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/1c16f91a0f1246778332e8f590974262.png)

被监控的虚拟机启动以后，我们就能够其他电脑上通Jconsole进行远程连接。

连接的过程例如以下：

1.打开cmd，输入jconsole，就会出现jconsole控制台，

然后，我们输入要被监控的Java虚拟机的IP地址和port号，如果输入正确，连接button就上生效如果设计的监控port号为8082，连接的IP为：10.20.618.11（这个需要你防火墙以及端口都处于开放状态），例如以下图所看到的：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/b2f94d65785a4acfbc85d5535889fbab.png)

点击连接后，就会进入到正常的显示界面，说明就连接成功了。

#### JConsole的显示界面：

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/4f1e3fa6d8994f3892000fc84208b20f.png)

**概述** ：记录了“堆内存使用情况”、“线程”、“类”、“CPU使用情况”共四个资源的实时情况；

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/efb1b06e892f4a229ba574aad14fb713.png)

并且在时间范围可以选择从1分钟到1年的显示情况

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/1b9494333e5b49b5bc39053c1b3aab5a.png)

**内存**  ：可以选择查看“堆内存使用情况”、“非堆内存使用情况”、“内存池"PS Eden Space"”等内存占用的实时情况；界面右下角还有图形化的堆一级、二级、三级缓存（从左到右）占用情况，当然，如果三级缓存被全部占用也就是很可能内存溢出啦！这时可以去查看服务器的tomcat日志，应该会有“outofmemory"的异常日志信息。界面右上角处还提供了一个“执行GC”的手动垃圾收集功能，这个也很实用~而且界面下方还有详细的GC信息记录。，整个界面提供了关于垃圾收集必须的各项基础指标查询。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/059844d8479d4b6895ddad61ac6bd895.png)

**线程** ：界面上部显示实时线程数目。下部还能查看到详细的每个进程及相应状态、等待、堆栈追踪等信息；

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/45c682b9d11e45418add74b5f1142b7c.png)

并且在右下角，我们还可以检测死锁的情况。如果当前线程没有出现死锁，那么会显示未出现死锁。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/73785f956bf447cf858f15255fd211a7.png)

但是如果出现了死锁，这里也会进行相应的检测。会直接显示死锁的页面，并且我们可以通过点击对应的线程来查看死锁的信息。

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/b6d4ddf4763f473b9e7416ad225677e6.png)

**类** ：显示“已装入类的数目”、“已卸载类的数目”信息；

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/ae89012cdda545aa884e405d3e68cee2.png)

**检测死锁**：
![在这里插入图片描述](https://img-blog.csdnimg.cn/8ded8c24d72041d4a52d3a1435d25594.png)

**VM摘要** ：显示服务器详细资源信息，包括：线程、类、OS、内存等；

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/8e41461bfa03410cb3ad8efedfd66bfc.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/18602db0ff9741ad8d4719359c8feaff.png)

**MBean** : 可在此页进行参数的配置。

*MBean就是被JMX管理的资源。* 一般有两种类型的MBean,标准的和动态的。 标准类型的MBean最简单,它能管理的资源(包括属性,方法,时间)必须定义在接口中,然后MBean必须实现这个接口。它的命名也必须遵循一定的规范,例如我们的MBean为User,则接口必须为UserMBean。 动态MBean必须实现javax.management.DynamicMBean接口,所有的属性,方法都在运行时定义。

这个一般情况下互联网交互式企业级开发用到的可能性没有那么高。因为现在JMX架构用得没那么多。

#### 测试垃圾回收案例：

```java
package com.example.jvmcase.test;


import com.google.common.collect.Lists;

import java.util.ArrayList;

public class JconsoleTest1 {
    //将bytes设置为全局变量
    public byte[] bytes = new byte[1024*1024];
    //测试内存的变化情况
    public static void main(String[] args) {
        try {
            Thread.sleep(3000);//为了能看到效果
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("开始..");
        fillHeap(1000);
    }

    public static void fillHeap(int count){
        ArrayList<JconsoleTest1> jts = Lists.newArrayList();

        for(int i=0;i<count;i++){
            try {
                //这里睡3S，出效果
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jts.add(new JconsoleTest1());
        }
    }
}


```

监控JVM内存情况，发现持续不回收，内存持续上升。

接下来，将bytes放进构造函数中，变成局部变量。如果没有使用的情况下，垃圾回收器是会光顾。现在再去看内存情况，发现内存在垃圾回收时候会形成波峰。

```java
 public  JconsoleTest2(){
        //将bytes设置为局部变量
        byte[] bytes = new byte[1024 * 1024];
    }
```

### jvisual vm

官网：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jvisualvm.html

官方描述：

> ```
> Description
> Java VisualVM is an intuitive graphical user interface that provides detailed information about Java technology-based applications (Java applications) while they are running on a specified Java Virtual Machine (JVM). The name Java VisualVM comes from the fact that Java VisualVM provides information about the JVM software visually.
> 
> Java VisualVM combines several monitoring, troubleshooting, and profiling utilities into a single tool. For example, most of the functionality offered by the standalone tools jmap, jinfo, jstat, and jstack were integrated into Java VisualVM. Other functionality, such as some that offered by the jconsole command, can be added as optional plug-ins.
> 
> Java VisualVM is useful to Java application developers to troubleshoot applications and to monitor and improve the applications' performance. Java VisualVM enables developers to generate and analyze heap dumps, track down memory leaks, perform and monitor garbage collection, and perform lightweight memory and CPU profiling. You can expand the Java VisualVM functionality with plug-ins. For example, most of the functionality of the jconsole command is available through the MBeans Tab and JConsole Plug-in Wrapper plug-ins. You can choose from a catalog of standard Java VisualVM plug-ins by selecting Tools and then Plugins in the Java VisualVM menus.
> 
> Start Java VisualVM with the following command:
> 
> %  jvisualvm <options>
> ```

**大致意思：**

Java VisualVM 是一个直观的图形用户界面，可在基于 Java 技术的应用程序（Java 应用程序）在指定的 Java 虚拟机 (JVM) 上运行时提供有关它们的详细信息。

Java VisualVM 将多个监控、故障排除和分析实用程序组合到一个工具中。例如，独立工具jmap、jinfo和提供jstat,的大部分功能jstack都集成到 Java VisualVM 中。其他功能，例如jconsole命令提供的一些功能，可以作为可选插件添加。

Java VisualVM 对于 Java 应用程序开发人员对应用程序进行故障排除以及监控和改进应用程序的性能非常有用。Java VisualVM 使开发人员能够生成和分析堆转储、跟踪内存泄漏、执行和监视垃圾收集以及执行轻量级内存和 CPU 分析。

使用以下命令启动 Java VisualVM：

% jvisualvm &#x3c;选项>

#### 监控本地Java进程

可以监控本地的java进程的CPU，类，线程等

#### 监控远端Java进程

（1）在visualvm中选中“远程”，右击“添加”

（2）主机名上写服务器的ip地址，比如39.100.39.63，然后点击“确定”

（3）右击该主机"39.100.39.63"，添加“JMX”，也就是通过JMX技术具体监控远端服务器哪个Java进程

（4）要想让服务器上的tomcat被连接，需要改一下Catalina.sh这个文件

> **注意下面的8998不要和服务器上其他端口冲突**

```shell
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -
Djava.rmi.server.hostname=39.100.39.63 -Dcom.sun.management.jmxremote.port=8998
-Dcom.sun.management.jmxremote.ssl=false -
Dcom.sun.management.jmxremote.authenticate=true -
Dcom.sun.management.jmxremote.access.file=../conf/jmxremote.access -
Dcom.sun.management.jmxremote.password.file=../conf/jmxremote.password"
```

（5）在../conf文件中添加两个文件jmxremote.access和jmxremote.password

**jmxremote.access**

```
guest readonly
manager readwrite
```

**jmxremote.password**

```
guest guest
manager manager
```

授予权限：chmod 600 *jmxremot*

（6）将连接服务器地址改为公网ip地址

```shell
hostname -i   查看输出情况
	172.26.225.240 172.17.0.1
vim /etc/hosts
	172.26.255.240 39.100.39.63
```

（7）设置上述端口对应的阿里云安全策略和防火墙策略

（8）启动tomcat，来到bin目录

```
./startup.sh
```

（9）查看tomcat启动日志以及端口监听

```
tail -f ../logs/catalina.out
lsof -i tcp:8080
```

（10）查看8998监听情况，可以发现多开了几个端口

```shell
lsof -i:8998    得到PID
netstat -antup | grep PID
```

（11）在刚才的JMX中输入8998端口，并且输入用户名和密码则登录成功

```
端口:8998
用户名:manager
密码:manager
```

 生成与分析dump文件：
VisualVM中监视和线程的右上角都有Dump按钮，可以对当前JVM的情况进行dump，dump下来后可以点击文件->装入，进行dump文件分析，同时还可以比较两个不同时间段的dump文件

这里不多展示，但是较为重要，记得自己点击用着看看

3.3.2 CPU与内存抽样
可以对某一时刻的CPU和内存进行快照，然后查看CPU突然飙高的原因，以及内存中类过多的问题。 

### arthas

> `github`：https://github.com/alibaba/arthas
>
> ```
> Arthas allows developers to troubleshoot production issues for Java
> applications without modifying code or restarting servers.
> ```
>
> Arthas 是Alibaba开源的Java诊断工具，采用命令行交互模式，是排查jvm相关问题的利器。

官网doc文档：https://arthas.aliyun.com/doc/

#### 3.3.3.1 下载安装

```shell
curl -O https://alibaba.github.io/arthas/arthas-boot.jar
java -jar arthas-boot.jar
or 
java -jar arthas-boot.jar -h
# 然后可以选择一个Java进程
```

#### 3.3.3.2 常用命令

> 具体每个命令怎么使用，大家可以自己查阅官网doc文档

```
version:查看arthas版本号
help:查看命名帮助信息
cls:清空屏幕
session:查看当前会话信息
quit:退出arthas客户端
---
dashboard:当前进程的实时数据面板
thread:当前JVM的线程堆栈信息
jvm:查看当前JVM的信息
sysprop:查看JVM的系统属性
---
sc:查看JVM已经加载的类信息
dump:dump已经加载类的byte code到特定目录
jad:反编译指定已加载类的源码
---
monitor:方法执行监控
watch:方法执行数据观测
trace:方法内部调用路径，并输出方法路径上的每个节点上耗时
stack:输出当前方法被调用的调用路径
......
```

### 



## 内存分析工具

### MAT（Memory Analyzer Tool）

MAT是一款非常强大的内存分析工具，在Eclipse中有相应的插件，同时也有单独的安装包。在进行内存分析时，只要获得了反映当前设备内存映像的hprof文件，通过MAT打开就可以直观地看到当前的内存信息。一般说来，这些内存信息包含：

* 所有的对象信息，包括对象实例、成员变量、存储于栈中的基本类型值和存储于堆中的其他对象的引用值。
* 所有的类信息，包括classloader、类名称、父类、静态变量等
* GCRoot到所有的这些对象的引用路径
* 线程信息，包括线程的调用栈及此线程的线程局部变量（TLS）

那么接下来  我们可以尝试去分析一下内存泄漏场景：

```java
package com.example.jvmcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TLController {
    @GetMapping(value = "/tl")
    public String tl(HttpServletRequest request) {
        ThreadLocal<Byte[]> tl = new ThreadLocal<Byte[]>();
        tl.set(new Byte[1024 * 1024]);
        return "ok";
    }
}
```

在这个代码中，很明显我们的代码没有去进行remove，所以会造成内存泄漏。而内存泄漏的堆积会导致内存溢出。所以我考虑用这个场景进行分析。

项目启动参数

```shell
-Xms100M -Xmx100M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=jvm.hprof 
```

这个时候hprof文件导出，一般生产环境中，我们的要考虑远程导出可能会导致系统卡断或者直接宕机。或者dump出的文件特别大，这个时候需要设置内存。

需要在mat目录下的MemoryAnalyzer.ini文件种改一下配置，最大占用内存设大一点，Xmx参数

```java
 -Xmx10240M            这个数字根据你文件大小设置
```

设置后重启生效

手动转储

```java
jmap -histo:live PID | more
获取到jvm.hprof文件，上传到指定的工具分析，比如MAT
```

### heaphero

[https://heaphero.io/](https://heaphero.io/)

### perfma

笨马是一个JVM调优工具，甚至会给你相应的JVM调优建议，但是他是一个收费工具，不过如果你仅仅是希望调优参数，可以使用试用版。但是他的调优建议都是简单的参数设置。

[https://console.perfma.com/](https://console.perfma.com/)

## 日志分析工具

> 要想分析日志的信息，得先拿到GC日志文件才行，所以得先配置一下，根据前面参数的学习，下面的配置很容易看懂。比如打开windows中的catalina.bat，在第一行加上

```
XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps 
-Xloggc:$CATALINA_HOME/logs/gc.log
```

设置JVM GC格式日志的主要参数包括如下8个：

1. -XX:+PrintGC 输出简要GC日志
2. -XX:+PrintGCDetails 输出详细GC日志
3. -Xloggc:gc.log 输出GC日志到文件
4. -XX:+PrintGCTimeStamps 输出GC的时间戳（以JVM启动到当期的总时长的时间戳形式）
5. -XX:+PrintGCDateStamps 输出GC的时间戳（以日期的形式，如 2020-04-26T21:53:59.234+0800）
6. -XX:+PrintHeapAtGC 在进行GC的前后打印出堆的信息
7. -verbose:gc : 在JDK 8中，-verbose:gc是-XX:+PrintGC一个别称，日志格式等价于：-XX:+PrintGC。不过在JDK 9中 -XX:+PrintGC被标记为deprecated。 -verbose:gc是一个标准的选项，-XX:+PrintGC是一个实验的选项，建议使用-verbose:gc 替代-XX:+PrintGC
8. -XX:+PrintReferenceGC 打印年轻代各个引用的数量以及时长

**开启GC日志** 多种方法都能开启GC的日志功能，其中包括：使用-verbose:gc或-XX:+PrintGC这两个标志中的任意一个能创建基本的GC日志（这两个日志标志实际上互为别名，默认情况下的GC日志功能是关闭的）使用-XX:+PrintGCDetails标志会创建更详细的GC日志。 推荐使用-XX:+PrintGCDetails标志（这个标志默认情况下也是关闭的）；通常情况下使用基本的GC日志很难诊断垃圾回收时发生的问题。

**开启GC时间提示** 除了使用详细的GC日志，我们还推荐使用-XX:+PrintGCTimeStamps或者-XX:+PrintGCDateStamps，便于我们更精确地判断几次GC操作之间的时间。这两个参数之间的差别在于时间戳是相对于0（依据JVM启动的时间）的值，而日期戳（date stamp）是实际的日期字符串。由于日期戳需要进性格式化，所以它的效率可能会受轻微的影响，不过这种操作并不频繁，它造成的影响也很难被我们感知。

**指定GC日志路径** 默认情况下GC日志直接输出到标准输出，不过使用-Xloggc:filename标志也能修改输出到某个文件。除了显式地使用-PrintGCDetails标志，否则使用-Xloggc会自动地开启基本日志模式。

使用日志循环（Log rotation）标志可以限制保存在GC日志中的数据量；对于需要长时间运行的服务器而言，这是一个非常有用的标志，否则累积几个月的数据很可能会耗尽服务器的磁盘。

**开启日志滚动输出** 通过-XX:+UseGCLogfileRotation -XX:NumberOfGCLogfiles=N -XX:GCLogfileSize=N标志可以控制日志文件的循环。 默认情况下，UseGCLogfileRotation标志是关闭的。它负责打开或关闭GC日志滚动记录功能的。要求必须设置 -Xloggc参数开启UseGCLogfileRotation标志后，默认的文件数目是0（意味着不作任何限制），默认的日志文件大小是0（同样也是不作任何限制）。因此，为了让日志循环功能真正生效，我们必须为所有这些标志设定值。

需要注意的是：

- 设置滚动日志文件的大小，必须大于8k。当前写日志文件大小超过该参数值时，日志将写入下一个文件
- 设置滚动日志文件的个数，必须大于等于1
- 必须设置 -Xloggc 参数

**开启语句**

```
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/home/hadoop/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=512k
```

**其他有用参数** -XX:+PrintGCApplicationStoppedTime 打印GC造成应用暂停的时间 -XX:+PrintTenuringDistribution 在每次新生代 young GC时,输出幸存区中对象的年龄分布

**日志含义**

![img](https://pics1.baidu.com/feed/72f082025aafa40f43fc7296ca47fc4778f0196a.png@f_auto?token=be6a5025fd54a20e118ae098fbc2b84e)

21.png

![img](https://pics3.baidu.com/feed/838ba61ea8d3fd1fc6eb5aaa506dda1794ca5f68.png@f_auto?token=3868bff6fd6cb9973a9fc8cbf00e9f0e)

22.png

### 6.4.4 -XX:CMSFullGCsBeforeCompaction

CMSFullGCsBeforeCompaction 说的是，在上一次CMS并发GC执行过后，到底还要再执行多少次full GC才会做压缩。默认是0，也就是在默认配置下每次CMS GC顶不住了而要转入full GC的时候都会做压缩。 如果把CMSFullGCsBeforeCompaction配置为10，就会让上面说的第一个条件变成每隔10次真正的full GC才做一次压缩（而不是每10次CMS并发GC就做一次压缩，目前VM里没有这样的参数）。这会使full GC更少做压缩，也就更容易使CMS的old gen受碎片化问题的困扰。 本来这个参数就是用来配置降低full GC压缩的频率，以期减少某些full GC的暂停时间。CMS回退到full GC时用的算法是mark-sweep-compact，但compaction是可选的，不做的话碎片化会严重些但这次full GC的暂停时间会短些。这是个取舍。

```
-XX:+UseCMSCompactAtFullCollection
-XX:CMSFullGCsBeforeCompaction=10
```

两个参数必须同时使用才能生效。

### 6.4.5 -XX:HeapDumpPath

堆内存出现OOM的概率是所有内存耗尽异常中最高的，出错时的堆内信息对解决问题非常有帮助，所以给JVM设置这个参数(-XX:+HeapDumpOnOutOfMemoryError)，让JVM遇到OOM异常时能输出堆内信息，并通过（-XX:+HeapDumpPath）参数设置堆内存溢出快照输出的文件地址，这对于特别是对相隔数月才出现的OOM异常来说尤为重要。 这两个参数通常配套使用：

```
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./
```

### 6.4.6 -XX:OnOutOfMemoryError

```
-XX:OnOutOfMemoryError=
"/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/binjconsole"
```

表示发生OOM后，运行jconsole程序。这里可以不用加“”，因为jconsole.exe路径Program Files含有空格。

利用这个参数，我们可以在系统OOM后，自定义一个脚本，可以用来发送邮件告警信息，可以用来重启系统等等。

### 6.4.7 XX:InitialCodeCacheSize

JVM一个有趣的，但往往被忽视的内存区域是“代码缓存”，它是用来存储已编译方法生成的本地代码。代码缓存确实很少引起性能问题，但是一旦发生其影响可能是毁灭性的。如果代码缓存被占满，JVM会打印出一条警告消息，并切换到interpreted-only 模式：JIT编译器被停用，字节码将不再会被编译成机器码。因此，应用程序将继续运行，但运行速度会降低一个数量级，直到有人注意到这个问题。

就像其他内存区域一样，我们可以自定义代码缓存的大小。相关的参数是-XX:InitialCodeCacheSize 和- XX:ReservedCodeCacheSize，它们的参数和上面介绍的参数一样，都是字节值。

### 6.4.8 -XX:+UseCodeCacheFlushing

如果代码缓存不断增长，例如，因为热部署引起的内存泄漏，那么提高代码的缓存大小只会延缓其发生溢出。

为了避免这种情况的发生，我们可以尝试一个有趣的新参数：当代码缓存被填满时让JVM放弃一些编译代码。通过使用-XX:+UseCodeCacheFlushing 这个参数，我们至少可以避免当代码缓存被填满的时候JVM切换到interpreted-only 模式。



#### 不同收集器日志

这样使用startup.bat启动tomcat的时候就能够在当前目录下拿到gc.log文件，可以看到默认使用的是ParallelGC。

（1）Parallel GC

> 【吞吐量优先】
>
> ```
> 2019-06-10T23:21:53.305+0800: 1.303: [GC (Allocation Failure) [PSYoungGen: 65536K[Young区回收前]->10748K[Young区回收后](76288K[Young区总大小])] 65536K[整个堆回收前]->15039K[整个堆回收后](251392K[整个堆总大小]), 0.0113277 secs] [Times: user=0.00 sys=0.00, real=0.01 secs] 
> ```
>
> > `注意`如果回收的差值中间有出入，说明这部分空间是Old区释放出来的



（2）CMS

> 【停顿时间优先】
>
> ```
> 参数设置：-XX:+UseConcMarkSweepGC -Xloggc:cms-gc.log
> ```

重启tomcat获取gc日志，这里的日志格式和上面差不多，不作分析。

（3）G1

G1日志格式参考链接：https://blogs.oracle.com/poonam/understanding-g1-gc-logs

> 【停顿时间优先】
>
> ```
> 参数设置：-XX:+UseG1GC -Xloggc:g1-gc.log
> ```
>
> ```shell
> -XX:+UseG1GC  # 使用了G1垃圾收集器
> 
> # 什么时候发生的GC，相对的时间刻，GC发生的区域young，总共花费的时间，0.00478s，
> # It is a stop-the-world activity and all
> # the application threads are stopped at a safepoint during this time.
> 2019-12-18T16:06:46.508+0800: 0.458: [GC pause (G1 Evacuation Pause)
> (young), 0.0047804 secs]
> 
> # 多少个垃圾回收线程，并行的时间
> [Parallel Time: 3.0 ms, GC Workers: 4]
> 
> # GC线程开始相对于上面的0.458的时间刻
> [GC Worker Start (ms): Min: 458.5, Avg: 458.5, Max: 458.5, Diff: 0.0]
> 
> # This gives us the time spent by each worker thread scanning the roots
> # (globals, registers, thread stacks and VM data structures).
> [Ext Root Scanning (ms): Min: 0.2, Avg: 0.4, Max: 0.7, Diff: 0.5, Sum: 1.7]
> 
> # Update RS gives us the time each thread spent in updating the Remembered
> Sets.
> [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
> ...
> 
> # 主要是Eden区变大了，进行了调整
> [Eden: 14.0M(14.0M)->0.0B(16.0M) Survivors: 0.0B->2048.0K Heap:
> 14.0M(256.0M)->3752.5K(256.0M)]
> ```

![](E:/图灵课堂/JVM/images/55.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/1a36df96b353454c9d27722b7d87fdc4.png)

### GCViewer

java -jar gcviewer-1.36-SNAPSHOT.jar

![](E:/图灵课堂/JVM/images/56.png)![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/be199bbcb4534c08aa852db802f4d9bf.png)

### gceasy

http://gceasy.io

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/2d2e103191594bedacae1ddbcfc84976.png)

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/1b2f0255426a40009c8deaa1f72d3dfa.png)

### gcplot

https://it.gcplot.com/

这个是young区的信息

![image.png](https://fynotefile.oss-cn-zhangjiakou.aliyuncs.com/fynote/fyfile/1463/1656048868088/16e2219fe38d4735b718db1c12a54025.png)

## JVM调优

性能调优包含多个层次，比如:架构调优、代码调优、JVM调优、数据库调优、操作系统调优等。 架构调优和代码调优是JVM调优的基础，其中架构调优是对系统影响最大的。

### JVM调优原则

#### **预调优原则**

优先架构参数调优和代码调优，JVM优化是不得已的手段，大多数的Java应用不需要进行JVM优化

3.2 **堆设置**

参数-Xms和-Xmx，通常设置为相同的值，避免运行时要不断扩展JVM内存，建议扩大至3-4倍FullGC后的老年代空间占用。

**3.3 年轻代设置**

参数-Xmn，1-1.5倍FullGC之后的老年代空间占用。

避免新生代设置过小，当新生代设置过小时，会带来两个问题：一是minor GC次数频繁，二是可能导致 minor GC对象直接进老年代。当老年代内存不足时，会触发Full GC。 避免新生代设置过大，当新生代设置过大时，会带来两个问题：一是老年代变小，可能导致Full GC频繁执行；二是 minor GC 执行回收的时间大幅度增加。

**3.4 老年代设置**

- 注重低延迟的应用

- - 老年代使用并发收集器，所以其大小需要小心设置，一般要考虑并发会话率和会话持续时间等一些参数
  - 如果堆设置偏小，可能会造成内存碎片、高回收频率以及应用暂停
  - 如果堆设置偏大，则需要较长的收集时间

- 吞吐量优先的应用 一般吞吐量优先的应用都有一个较大的年轻代和一个较小的老年代。原因是，这样可以尽可能回收掉大部分短期对象，减少中期的对象，而老年代尽可能存放长期存活对象

**3.5 方法区设置**

基于jdk1.7版本，永久代：参数-XX:PermSize和-XX:MaxPermSize； 基于jdk1.8版本，元空间：参数 -XX:MetaspaceSize和-XX:MaxMetaspaceSize； 通常设置为相同的值，避免运行时要不断扩展，建议扩大至1.2-1.5倍FullGc后的永久带空间占用。

**GC设置**

**GC发展阶段**

SerialParallel（并行) CMS（并发) G1，ZGC 截至jdk1.8 ，一共有7款不同垃圾收集器。每一款不同的垃圾收集器都有不同的特点，在具体使用的时候，需要根据具体的情况选择不同的垃圾回收器

**G1的适用场景**

- 面向服务端应用，针对具有大内存、多处理器的机器。(在普通大小的堆里表现并不惊喜)

- 最主要的应用是需要低GC延迟并具有大堆的应用程序提供解决方案(G1通过每次只清理一部分而不是全部Region的增量式清理来保证每次GC停顿时间不会过长)

- 在堆大小约6GB或更大时，可预测的暂停时间可以低于0.5秒

- 用来替换掉JDK1.5中的CMS收集器，以下情况，使用G1可能比CMS好

- - 超过50% 的java堆被活动数据占用
  - 对象分配频率或年代提升频率变化很大
  - GC停顿时间过长(大于0.5至1秒)

- 从经验上来说，整体而言：

- - 小内存应用上，CMS大概率会优于 G1；
  - 大内存应用上，G1则很可能更胜一筹。 这个临界点大概是在 6~8G 之间（经验值）

####  其他收集器适用场景

- 如果你想要最小化地使用内存和并行开销，请选择Serial Old(老年代) + Serial(年轻代)
- 如果你想要最大化应用程序的吞吐量，请选择Parallel Old(老年代) + Parallel(年轻代)
- 如果你想要最小化GC的中断或停顿时间，请选择CMS(老年代) + ParNew(年轻代)

#### JVM调优步骤

##### 监控分析

分析GC日志及dump文件，判断是否需要优化，确定瓶颈问题点。

##### 生成GC日志

常用参数部分会详细讲解如何生成GC日志

##### 产生dump文件

##### JVM的配置文件中配置

JVM启动时增加两个参数:

```
# 出现OOME时生成堆dump:
-XX:+HeapDumpOnOutOfMemoryError
# 生成堆文件地址：
-XX:HeapDumpPath=/home/hadoop/dump/
```

####  jmap生成

发现程序异常前通过执行指令，直接生成当前JVM的dump文件

```
jmap -dump:file=文件名.dump [pid]
# 9257是指JVM的进程号
jmap -dump:format=b,file=testmap.dump 9257
```

第一种方式是一种事后方式，需要等待当前JVM出现问题后才能生成dump文件，实时性不高； 第二种方式在执行时，JVM是暂停服务的，所以对线上的运行会产生影响。

所以建议第一种方式。

#### 第三方可视化工具生成

#### 判断

如果各项参数设置合理，系统没有超时日志或异常信息出现，GC频率不高，GC耗时不高，那么没有必要进行GC优化，如果GC时间超过1-3秒，或者频繁GC，则必须优化。 遇到以下情况，就需要考虑进行JVM调优：

- 系统吞吐量与响应性能不高或下降；
- Heap内存（老年代）持续上涨达到设置的最大内存值；
- Full GC 次数频繁；
- GC 停顿时间过长（超过1秒）；
- 应用出现OutOfMemory等内存异常；
- 应用中有使用本地缓存且占用大量内存空间；

#### 确定目标

调优的最终目的都是为了应用程序使用最小的硬件消耗来承载更大的吞吐量或者低延迟。 jvm调优主要是针对垃圾收集器的收集性能优化，减少GC的频率和Full GC的次数，令运行在虚拟机上的应用能够使用更少的内存、高吞吐量、低延迟。

下面列举一些JVM调优的量化目标参考实例，注意：不同应用的JVM调优量化目标是不一样的。

- 堆内存使用率<=70%;
- 老年代内存使用率<=70%;
- avgpause<=1秒;
- Full GC次数0或avg pause interval>=24小时 ;

#### 调整参数

调优一般是从满足程序的内存使用需求开始的，之后是时间延迟的要求，最后才是吞吐量的要求。 要基于这个步骤来不断优化，每一个步骤都是进行下一步的基础，不可逆行之。

#### 对比调优前后指标差异

#### 重复以上过程

#####  应用

找到合适的参数，先在单台服务器上试运行，然后将这些参数应用到所有服务器，并进行后续跟踪。



#### CPU飙升排查方案

1.使用top命令查看cpu情况

2.找出哪一进程占用cpu飙升

3.ps命令(ps H -p <PID>)查看进程中的线程信息

4.使用jstack命令查看进程中哪些线程出现问题

5.定位问题，修改代码



#### 内存泄露排查方案

1.获取堆内存快照dump

(配置启动参数-XX:+HeapDumpOnOutOfMemoryError,-XXHeapDumpPath或者jmap工具jmap -dump:format=b,file=heap.hprof PID)

2.使用visualVM或者map工具分析dump文件

3.查看堆信息情况，通过GCRoots引用链定位内存溢出问题

4.修改代码或修改堆/方法区大小(-Xms,-Xmx 或-XX:PermSize,-XX:MaxPermSize)解决泄露







