import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class GoLike {

    public static void go(Runnable runnable) {
        Thread.ofVirtual().start(runnable);
    }

    public static class Channel<T> {
        BlockingQueue<T> internal;
        boolean closed = false;

        public Channel(BlockingQueue<T> internal) {
            this.internal = internal;
        }

        public void push(T value) {
            if (closed) throw new IllegalStateException("Channel is closed");

            try {
                internal.offer(value, 1000, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public T take() {
            if (internal.isEmpty() && closed) throw new IllegalStateException("Channel is closed");

            try {
                return internal.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            closed = true;
        }

    }

    public static class WaitGroup {
        CountDownLatch cdlr;

        WaitGroup(int count) {
            cdlr = new CountDownLatch(count);
        }

        public void await() {
            try {
                cdlr.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void done() {
            cdlr.countDown();
        }
    }

    public static <T> Channel<T> makeChan(Class<T> type) {
        return makeChan(type, 1);
    }

    public static <T> Channel<T> makeChan(Class<T> type, int capacity) {
        return new Channel<>(new ArrayBlockingQueue<>(capacity));
    }

    public static void close(Channel<?> channel) {
        channel.close();
    }


    public static void main(String[] args) throws Exception {
        var mutex = new ReentrantLock();
        mutex.lock();
        mutex.unlock();

        var ch = makeChan(String.class);
        go(() -> {
            for (int i = 0; i < 10; i++) {
                ch.push(Integer.toString(i));
            }
            close(ch);
        });

        for (int i = 0; i < 10; i++) {
            System.out.println(ch.take());
        }

//        IllegalStateException: Channel is closed
//        System.out.println(ch.take());

        var ch2 = makeChan(String.class, 20);

        var wg = new WaitGroup(5);
//        go(() -> {
        for (int i = 0; i < 10; i++) {

            int finalI = i; // garbage :C
            go(() -> {
                if (finalI < 6) {
                    wg.done();
                }

                ch2.push(Integer.toString(finalI));
            });
        }
        close(ch2);
//        });

        wg.await();
    }


//    func fibonacci(c, quit chan int) {
//        x, y := 0, 1
//        for {
//            select {
//                case c <- x:
//                    x, y = y, x+y
//                case <-quit:
//                    fmt.Println("quit")
//                    return
//            }
//        }
//    }

    void fibonacci(Channel<Integer> c, Channel<Integer> quit) {
        select(c, quit, channel -> {
            if (channel == c) {
                c.push(1);
            }
            if (channel == quit) {
                return;
            }
        });
    }

    private void select(Channel<Integer> c, Channel<Integer> quit, Consumer<Channel<?>> proc) {
        c.take(); // todo different listenable interface?
        // or goroutine trying to take from each one?:D
    }
}

// TODO defer can be done like lombok's https://projectlombok.org/features/Cleanup :D


// Selector implementation available in java :)
// https://github.com/daichi-m/jach/blob/develop/src/main/java/io/github/daichim/jach/channel/selector/Selector.java
// Api:
// https://github.com/daichi-m/jach/blob/develop/samples/src/main/java/io/github/daichim/jachsamples/SelectWithDefault.java