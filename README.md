A poor man Dependency Injection
Dependency Injection (DI) has been around for a while now.
A typical use case would be, for instance, the same piece of code is used by two scenarios. 
Let’s take, for example, a system that hooks to a data source and does something with the data.
On Sunday, the system hooks to Goolge, and on Monday to Facebook.
In this case, we usually create an interface and two implementations. 
Then somehow push the implementation into the code. On Sunday we invoke the first implementation, and on Monday the second implementation.
A more elegant solution is to use java reflection to inject the implementation via Class.forName(). 
That was true back than when we didn’t have frameworks such as Guice (https://github.com/google/guice) and others.
We only need to add something like this:

public interface Communication{
  
    public Data getData(String locator);
}


Public class GoogleCommunication implements Communication{

	public Data getData(String locator){

		// Fetch the data form Google somehow.
		return data;
	}
}

Public class FacebookCommunication implements Communication{

	public Data getData(String locator){

		// Fetch the data from Facebook somehow.
		return data;
	}
}

On top of that, we need also to create a Module:
public class BasicModule extends AbstractModule {
  
    @Override
    protected void configure() {
        bind(Communicator.class).to(GoogleCommunicator.class);
    }
}

And to glue it altogether:
public static void main(String[] args){

    Injector injector = Guice.createInjector(new BasicModule());
    Communication comms = injector.getInstance(Communication.class);
}

We can use Annotation for that as well:

@Inject @Named("GoogleCommunicator")
Communicator communicator;
 
Guice is good, actually very good, but in some cases I find this too verbal for me when it comes to small projects, or one that barely usage injection.
So I sat down and wrote my own ultra-light DI framework, it lacks the fancy interface, and it’s very basic, but I ended up with this piece of code:
Integer integer = Injector.of("java.lang.Integer","0");

That’s it! A single line of code.

We can also annotate the method:

@Inject(name="java.lang.Integer",values={"0"})
public void someMethod(){
    // Using the annotation above.
    Integer integer = Injector.of();
}

Or for a few local variables:

@Inject(name="java.lang.Integer",values={"0"})
@Inject(name="java.lang.Error",values={"Dummy 1"})
public void someMethod(){
     // Using the annotations above.
     Integer integer = Injector.of(0);
     Error e2 = Injector.of(1);
}

It’s much more elegant and very easy to maintain. 
But it comes in the price of giving up the supporting of inner dependencies.



