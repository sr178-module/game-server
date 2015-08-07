//package example;
//
//import com.lmax.disruptor.BatchEventProcessor;
//import com.lmax.disruptor.EventFactory;
//import com.lmax.disruptor.EventHandler;
//import com.lmax.disruptor.RingBuffer;
//import com.lmax.disruptor.SequenceBarrier;
//import com.lmax.disruptor.YieldingWaitStrategy;
//import com.lmax.disruptor.dsl.ProducerType;
//
//public class DisruptorTest {
//
//    public static void main(String[] args) {
//    	PersonHelp.start();
//        for(int i=0 ; i<20; i++){
//            Person p = new Person("jbgtwang"+i, i , "男", "1234566"+i);
//            //生产者生产数据
//            PersonHelp.produce(p);
//        }
//    }
//}
//
//
//class Person{
//private String name;
//private int age;
//private String gender;
//private String mobile;
//public String getName() {
//	return name;
//}
//
//public Person() {
//	super();
//}
//
//public Person(String name, int age, String gender, String mobile) {
//	super();
//	this.name = name;
//	this.age = age;
//	this.gender = gender;
//	this.mobile = mobile;
//}
//
//public void setName(String name) {
//	this.name = name;
//}
//public int getAge() {
//	return age;
//}
//public void setAge(int age) {
//	this.age = age;
//}
//public String getGender() {
//	return gender;
//}
//public void setGender(String gender) {
//	this.gender = gender;
//}
//public String getMobile() {
//	return mobile;
//}
//public void setMobile(String mobile) {
//	this.mobile = mobile;
//}
//public String toString(){
//	return "name="+name+",age="+age+",mobile="+mobile+",gender="+gender;
//}
//}
//
//class PersonEvent{
//	private Person person;
//	public Person getPerson() {
//		return person;
//	}
//	public void setPerson(Person person) {
//		this.person = person;
//	}
//	public final static EventFactory<PersonEvent> EVENT_FACTORY = new EventFactory<PersonEvent>() {
//		@Override
//		public PersonEvent newInstance() {
//			 return new PersonEvent();
//		}
//	};
//}
//
//class PersonEventHandler implements EventHandler<PersonEvent>{
//	@Override
//	public void onEvent(PersonEvent event, long sequence, boolean endOfBatch)
//			throws Exception {
//        Person person = event.getPerson();
//        System.out.println(person);
//	}
//}
//
//class PersonHelp{
//	private static PersonHelp instance = null;
//	private static boolean inited = false;
//	private final static int BUFFER_SIZE = 256;
//	private RingBuffer<PersonEvent> ringBuffer;
//	private SequenceBarrier sequenceBarrier;
//	private PersonEventHandler personEventHandler;
//	
//	private BatchEventProcessor<PersonEvent> batchEventProcessor;
//	
//	public PersonHelp(){
//		ringBuffer = RingBuffer.create(ProducerType.MULTI,PersonEvent.EVENT_FACTORY,BUFFER_SIZE, new YieldingWaitStrategy());
//		sequenceBarrier = ringBuffer.newBarrier();
//		personEventHandler = new PersonEventHandler();
//		 //事件处理器，监控指定ringBuffer,有数据时通知指定handler进行处理
//		batchEventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier, personEventHandler);
//		
//		ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
//	}
//	
//	public static void start(){
//		instance = new PersonHelp();
//		Thread thread = new Thread(instance.batchEventProcessor);
//		thread.start();
//		inited = true;
//	}
//	
//	public static void stop(){
//		   if(!inited){
//	             throw new RuntimeException("EncodeHelper还没有初始化！");
//	         }else{
//	             instance.doHalt();
//	       }     
//	}
//	private void doHalt() {
//        batchEventProcessor.halt();
//    }
//	
//	private void doProduce(Person person){
//		long sequence = ringBuffer.next();
//		ringBuffer.get(sequence).setPerson(person);
//		ringBuffer.publish(sequence);
//	}
//	
//    /**
//     * 生产者压入生产数据
//     * @param data
//     */
//    public static void produce(Person person){
//        if(!inited){
//            throw new RuntimeException("EncodeHelper还没有初始化！");
//        }else{
//            instance.doProduce(person);
//        }
//    }
//}
