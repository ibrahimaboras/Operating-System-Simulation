package main;

// public class Mutex {
//     private String resourceName;
//     private boolean isAvailable;

//     public Mutex(String resourceName) {
//         this.resourceName = resourceName;
//         this.isAvailable = true;
//     }

//     public String getResourceName() {
//         return resourceName;
//     }

//     public synchronized void acquire() {
//         while (!isAvailable) {
//             try {
//                 wait();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//         isAvailable = false;
//     }

//     public synchronized void release() {
//         isAvailable = true;
//         notifyAll();
//     }
// }


public class Mutex {
    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }

    public synchronized boolean isLocked() {
        return isLocked;
    }
}

//
//public class Mutex {
//    boolean locked = false;
//
//    public synchronized void acquire() throws InterruptedException {
//        while (locked) {
//            wait();
//        }
//        locked = true;
//    }
//
//    public synchronized void release() {
//        locked = false;
//        notifyAll();
//    }
//}
