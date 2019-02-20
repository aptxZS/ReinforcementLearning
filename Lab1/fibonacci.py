past_fib = {}


def fibonacci_dynamic(n):
    if n in past_fib:
        return past_fib[n]
    if n == 1 or n == 2:
        past_fib[n] = 1
        return 1
    total = fibonacci_dynamic(n-1) + fibonacci_dynamic(n-2)
    past_fib[n] = total
    return total


def fibonacci_recursive(n):
    if n == 1 or n == 2:
        return 1
    return fibonacci_recursive(n - 1) + fibonacci_recursive(n - 2)


print(fibonacci_dynamic(10))
print(fibonacci_dynamic(30))
print(fibonacci_recursive(10))
print(fibonacci_recursive(30))
