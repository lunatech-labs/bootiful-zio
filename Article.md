## Bootiful ZIO

### Spring booting with ZIO's ZLayer

> <h3>Why this write-up</h3>
> 
> I am fascinated by Spring Boot, it is one of the first frameworks I learnt when I started off, and I loved it. 
> But I've been hearing so much about ZIO, and I wanted to take a look at it, so the past few weeks I began studying 
> it and some concepts in the library, I found it even more fascinating. I noticed a shared style in the
> Spring Boot framework, which is a pattern that ZIO encourages (recommends). I would be pointing these out in this
> article. In case you love Spring Boot, enjoy the ease of working with it, and are looking for a similar framework in
> Scala, then you may want to try ZIO. It should be noted that the ideology of ZIO is beyond the scope of this write-up, 
> only a shared pattern in code organization and how services are implemented are what this article points out.

For brevity, I’d skip the meaning of these terms, Spring Boot and ZIO. I encourage you to look them up on your own.
What I will touch however, are some ideas in Spring which is a pattern encouraged in ZIO. </p>
I would cover just two of these concepts here, because they are what I have found out so far:
> 1. Class instantiation and dependency injection
> 2. Coding to interface

### <u> Similarity 1: Class instantiation and dependency injection. </u>

I’d start with a popular quote, which I heard from a friend sometime ago, says that "Spring is a bag of BEANS".
BEANS, a bunch of objects instantiated and managed by the Spring framework whenever a Spring application starts.
These classes are supplied to other classes where they are needed via the spring’s dependency injection mechanism.
We will not cover in details how this works. It is worth mentioning that you can register classes you want Spring
to manage using various annotations. Hence, an easy way to achieve dependency injection is by auto wiring 
a given class as field in other class(es) where it is needed. Too much talk, here is a Snippet. </p>
```
@Component
public class IndependentA {

    /** With bunch of A related methods */
}
```


```
@Component
Public class DependentB {

    @autowired
    private IndependentA independentA;

    /** With bunch of B related methods */
}
```
> <h4> What happens: </h4> Spring creates and manages instances of the classes A and B, because they are marked with the
> <code>@Component</code> annotation. Instances of class A and B are called BEANS collectively, BEAN for singular. 
> At runtime Spring supplies A to B (injection) because class A has been specified as a field in B and marked with <code> @autowired </code>.
> This is in fact a naive (not entirely recommended) way of how you can achieve BEAN instantiation and dependency injection in Spring. 

At the other side, let’s talk ZIO. Well, the ZIO library has this cool data structure called the ZLayer, that allows you
build your application in layers, like an onion. This helps you achieve similar thing, in a smarter fashion. </p>

> <h4> How does it work </h4> 
> Simple, you need to have companion objects where concrete classes (implementations) are turned into
> layers (materialised and stored as values), then during your program execution (i.e, where your ZIO effects run),
> you supply these values in the environment of that ZIO where it is needed. You explicitly help ZIO take care to put 
> these implementations into the environments of ZIO(s) that needs these implementations.
Below are some snippets of code that shows how to achieve this. </p>

```
trait TraitA {
    /** With bunch of A related methods, unimplemented */
}

case class AImpl() extends TraitA {
    /** With bunch of A related methods, all implemented */
}

object A extends Accessible[TraitA] {
    val layerA = (AImpl.apply _).toLayer //At this point, we have a layer, which is in fact a concrete implementation of A (class instantiation)
}
```
Say we have a class B that needs an instance of A, and we want to use ZIO to achieve dependency injection. </p>
```
trait TraitB {
    /** With bunch of B related methods, unimplemented */
}

case class BImpl(a: TraitA) extends TraitB {
    /** With bunch of B related methods, all implemented */
}

object B extends Accessible[TraitB] {
    val layerB = (BImpl.apply _).toLayer //At this point, we have a layer, which is in fact a concrete implementation of B (class instantiation)
}
```
Observe that while defining the case class `BImpl`, we have a parameter in its constructor of type `TraitA`. This is a 
dependency that we want to inject. Moving forward, we would see how specifying this interface as dependency will allow 
us to inject whatever concrete implementation (layers) of `TraitA` we want. </p>

> <h4> NB: </h4> For this to work in practice, you need to code to interface. This leads us to the next similarity 
> (i.e, Coding to interface)

### <u> Similarity 2: Coding to Interfaces </u>
Coding to interface(s) is a common practise in Java and Spring Boot. Coding to interface provides us an excellent 
abstraction which in turn yields the following benefits: </p>
<ul>
    <li> Your implementation logics are seperated from their definitions, hence they are hidden to users of the interface </li>
    <li> You can have multiple implementations to a single interface </li>
    <li> It is easy to test when you code to interfaces </li>
    <li> A class can implement multiple interfaces. In a way, this solves the problem that a class cannot inherit 
    from multiple classes on the JVM </li>
    <li> We get polymorphism for free, since we can easily replace an interface with a concrete class that implements it.
    This is in reality how most frameworks are able to replace an interface which is injected in a class with a concrete
    implementation of it wherever it is needed</li>
</ul>
The aforementioned are just a few of the benefits of coding to interface.

> <h4> How to do this: </h4>
> Create interfaces, define methods within it and then provide implementation(s) to these interfaces. 
> When you need to inject class A into another class B, then inject A's interface into a concrete implementation of B's interface. 
> Don’t worry Spring will look for an implementation of A and use it wherever it is needed in B.
> Oh, and if there are multiple implementations, don’t worry, spring have mechanisms that allow you
> name implementations and be more explicit on which you want injected into where. 

With this in mind, let's refine the Spring snippet above just a little to follow this principle

```
public interface InterfaceA {

    /** Bunch of A related unimplemented methods */
}
```

```
@Service
public class A implements InterfaceA {

    /** Provides implementation to the bunch of methods specified by the interface A */
}
```

Assuming B respect same pattern as above, but this time in an implementation of B, it needs A, we can do the following

```
@Service
Public class B implements InterfaceB {
    
    //See the line below, we inject the interface and trust on spring to provide a bean containing implementations to interface A
    @autowired
    private InterfaceA interfaceA;

    /** Provides implementation to the bunch of methods specified by the interface B */
}
```

Now to point out that ZIO's ZLayer preaches similar concept and is in fact called the SERVICE pattern. 
> SERVICE pattern means coding to interface(s), instantiating concrete classes as layers in companion objects of 
> interfaces, and finally injecting these layers in the environment of any effect where they're needed.

<h4> The service pattern follows these simple rules </h4>
1. If you ever want to create a service, then create traits

```
trait TraitA {

    /** Bunch of A unimplemented methods */
}
```

2. Create a companion object for this trait, where you will materialise concrete classes of it as layers,
only this time you have to extend `ZIO#Accessible`

```
object TraitA extends Accessible[TraitA] {
   
   //We now create vals holding a concrete implementation (layers)
   val live = (A.apply _).toLayer
   val test = (AnotherA.apply _).toLayer
}
```

3. Create case classes that extends (implements) this trait. 
These are in fact the concrete implementations that are stored in vals as layers above
```
case class A() extends TraitA {
	
    /** Provides implementation to the bunch of methods specified by the trait A */
}
```

4. Say you have another trait DependentTraitB following ZIO SERVICE pattern as described above. 
However, one of its implementations (case class) needs TraitA, 
then supply TraitA to that case class as a parameter.

```
case class DependentB(traitA: TraitA) extends TraitB {

    /** Provides implementation to the bunch of methods specified by the trait B */
}
```

Notice how you easily stick to coding against interfaces, during definition, implementation and injecting 
interfaces to other implementations where they are needed. Note that ZIO won’t choose an implementation on your behalf 
for an effect to run. It will only tell you at compile time to choose an implementation which should be in scope of the effect being run

```
Val computeAverage = for {
    b = ZIO.service[DependentB]
    avg <- b.compute(10, 3) //Assuming we had a compute method which takes two ints and gives a UIO[Double]
} yield avg
```

In the main method of our ZIOApp, we can call our program (computeAverage) as follows: 

`computeAverage.provide(…).exitCode`

Notice that in the braces, we have to inject layers (the concrete classes which we have assigned to vals within our companion objects) 
as environment which will be used by the effect. In our case we need DependentTraitB since we are working with it.

```
computeAverage.provide(
    DependentTraitB.live
).exitCode
```

ZIO tells you that “DependentTraitB.live” relies on some trait "TraitA", please provide an implementation.
Looking up to the companion object for TraitA, there are different implementations, you just have to choose one and supply

```
computeAverage.provide(
    DependentTraitB.live,
    TraitA.live //Notice here, that nothing stops us from injecting the test version of TraitA, i.e “TraitA.test”
).exitCode
```

Very simple, but powerful and even more clear, because this time, you won't just be trusting ZIO to do dependency injection for you,
you will be trusting you to instruct ZIO on how and where to instantiate a layer, where and which layer to inject into what effect.
If you create an effect that needs a layer, but it couldn't find it in its environment, then you trust ZIO to tell you this at compile time.

> This is the amount control you get, this is you Spring Booting with ZIO in a fancy and more powerful way. 

True these snippets does not do so much, if you are curious to see an example, I have attached one to this write-up, 
<a href = "https://github.com/lunatech-labs/bootiful-zio"> here </a>. <p>

I have chosen not to compare their differences in mechanism, because I just don’t want to discuss here how spring 
instantiates its classes. However, you can see how ZIO (or You instruct ZIO) to turn your implementation classes into 
layers, and you get to choose which layer you pass into what environment for an effect to run.
Many thanks to the Kit Langton (author of ZIO magic) which is in fact the magic behind this simplicity.

A better title would have been beautiful ZIO, but I needed to tell folks like me who love the spring world that 
porting could be fun. IMO, I'd say that ZIO is to Scala what Spring boot is to Java.
