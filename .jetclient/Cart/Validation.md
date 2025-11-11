```toml
name = 'Validation'
method = 'POST'
url = 'http//localhost:8080/api/cart/validate'
sortWeight = 1000000
id = '42898021-7b7d-40e3-adb2-bd9d4dee512b'

[body]
type = 'JSON'
raw = '''
{
  "cartId": "cart-123",
  "amount": 99.99,
  "currency": "EUR",
  "paymentMethod": "CREDIT_CARD",
  "customer": {
    "customerId": "cust-001",
    "email": "test@example.com",
    "name": "Jean Dupont"
  }
}'''
```
