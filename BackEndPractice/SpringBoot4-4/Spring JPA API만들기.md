# Spring JPA API만들기

JPA를 이용해서 API를 만들어보자.

Dto를 먼저 만들어준다..(Data Transfer Object)

Dto를 이용해서 엔티티를 한방에 정의해줬다.

## OrderConverter만들기

엔티티의 모든 것(Order, Item, Member)에 대해서 Entity를 Dto로, Dto를 Entity로 만들어준다.

코드는 너무 많기때문에 대표 몇개만 적는다.

```java
public Order convertOrder(OrderDto orderDto) {
        //DTO->Entity로 바꿔주는 메소드
        //영속성과 관계없는 객체를 준영속상태로 만들어주기 위해!
        Order order = new Order();
        order.setUuid(orderDto.getUuid());
        order.setMemo(orderDto.getMemo());
        order.setOrderStatus(orderDto.getOrderStatus());
        order.setOrderDatetime(LocalDateTime.now());
        order.setCratedAt(LocalDateTime.now());
        order.setCreatedBy(orderDto.getMemberDto().getName());

        order.setMember(this.convertMember(orderDto.getMemberDto()));
        this.convertOrderItems(orderDto).forEach(order::addOrderItem);

        return order;
    }
```

```java
private OrderItemDto convertOrderItemDto (OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .itemDtos(orderItem.getItems().stream()
                        .map(this::convertItemDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
```

## OrderService 만들기

레포지토리를 Autowired로 넣고,

Converter도 Autowired로 넣는다.

### Save()

```java
@Transactional
public String save(OrderDto dto){
    //transaction은 @Transactional로 처리 가능
    //1. dto->entity 변환(준영속상태)
    Order order = orderConverter.convertOrder(dto);
    //2. orderRepository.save(entity) (영속화)
    Order entity = orderRepository.save(order);
    //3. 결과 반환
    return entity.getUuid();

}
```

트랜젝션 처리를 해주고,

dot→entity변환 해준다. 그리고 리턴!

### findOne()

```java
@Transactional
    public OrderDto findOne(String uuid) throws NotFoundException {

        //1.조회를 위한 키값 인자로 받기
        //2.orderRepository.findById(uuid)->조회(영속화된 엔티티)
        return orderRepository.findById(uuid)
                .map(orderConverter::convertOrderDto)
                .orElseThrow(()-> new NotFoundException("주문을 찾을 수 없다."));
        //3. entity->dto(트랜젝션 밖으로 벗어나면 dto로 바꿔야함)

    }
```

uuid를 dto로 바꿔서 리턴하는데, map을 이용한다.

### findAll()

```java
@Transactional
    public Page<OrderDto> findAll(Pageable pageable){
        return orderRepository.findAll(pageable)
                .map(orderConverter::convertOrderDto);
    }
```

마찬가지로 선언해준다.

## OrderServiceTest

처음에 Order와 OrderItem,member를 선언하고, 메소드를 이용해서 뽑아낸것과 선언한 값이 같은지 체크.

### FindOneTest()

```java
@Test
void findOneTest() throws NotFoundException{
    //Given
    String orderUuid = uuid;

    //When
    OrderDto one = orderService.findOne(uuid);

    //Then
assertThat(one.getUuid()).isEqualTo(orderUuid);
}
```

다른것도 같은 방식으로 진행한다.