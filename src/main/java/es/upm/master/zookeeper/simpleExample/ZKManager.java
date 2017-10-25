package es.upm.master.zookeeper.simpleExample;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

        import org.apache.zookeeper.CreateMode;
        import org.apache.zookeeper.KeeperException;
        import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZKUtil;


        import org.apache.zookeeper.Watcher.Event.KeeperState;

/*first of all, lets try to add a new ID under request/enroll
*/


public class ZKManager implements Watcher{
    private static Stat stat;
    private static ZooKeeper zoo;

    public void ZKManager() throws KeeperException, InterruptedException, IOException {
        this.zoo = Test.zooConnect();    // Connects to ZooKeeper service

        destroyTree();
        constructTree();
        ZKWriter zkw=new ZKWriter();
        zkw.create("Santiago");


/*        WelcomeInterface welcome = new WelcomeInterface();
        welcome.initComponents(zoo);
        welcome.setVisible(true);*/

        //calling the methon create and giving the original connection zoo, and the user name
        /*create("Belus",zoo);
        create("Bebegimm",zoo);
        create("EnEsteVideeeeo",zoo);
        quit("Belus",zoo);*/
    }

    public void constructTree() throws KeeperException, InterruptedException {


        String auth = "user:pwd";
        zoo.addAuthInfo("digest",auth.getBytes());
        //create protected node
        zoo.create("/System", "znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //create znode sequential
        zoo.create("/System/Request", "znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //create znode sequential*/
        zoo.create("/System/Request/Enroll", "znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //create znode sequential
        zoo.create("/System/Request/Quit", "znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //create znode sequential
        zoo.create("/System/Registry", "znode".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);


        SetWatchers();

    }

    public void destroyTree() throws KeeperException, InterruptedException {

        Stat stat = zoo.exists("/System", true);
        System.out.println("this is the value of stat in the Initial path /System" + stat);
        if(stat!= null) {
            ZKUtil util = new ZKUtil();
            util.deleteRecursive(zoo, "/System");
        }
    }



    @Override
    public void process(WatchedEvent event) {

        if(event.getType() == EventType.NodeCreated){
            System.out.println(event.getPath() + " created");
            //if it comes from /enroll- run ZKManager registry

        }else if(event.getType() == EventType.NodeDeleted){
            System.out.println(event.getPath() + " deleted");
        }else if(event.getType() == EventType.NodeDataChanged){
            System.out.println(event.getPath() + " changed");

        }else if(event.getType() == EventType.NodeChildrenChanged){
            System.out.println(event.getPath() + " children created");
            //if it comes from /enroll- run ZKManager registry
            if(event.getPath().startsWith("/System/Request/Enroll")){
                List<String> children = null;
                try {
                    children = zoo.getChildren("/System/Request/Enroll",this);
                    Iterator<String> iterator = children.iterator();
                    while (iterator.hasNext()) {
                        register(iterator.next());
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




                //register(   ,zoo)


            }else if(event.getPath().startsWith("/System/Request/Quit")){


            }

        }else{
            System.out.println(event.getPath() + " what is this??");
        }

        SetWatchers();

    }

     //I created variables for paths.
    private String enroll = "/System/Request/Enroll/";
    private String registry = "/System/Registry/";
    private String quit = "/System/Request/Quit/";
    //private String receivedName;


    private void register(String name)throws KeeperException,
            InterruptedException {
        String path = registry + name;

        Stat stat = zoo.exists(path, true);
        if (stat == null) {
            System.out.println("User is not in Register- lets create it");
            boolean registerStatus = registerSystem(name);
            if(registerStatus){
                System.out.println("Registration is successful");
            }
            else{
                System.out.println("Registration is not successful");
            }
        }

    }


    public boolean registerSystem(String name) {
        String path = registry + name;
        boolean registerCode = true;
        //check register exists

        try {
            String auth = "user:pwd";
            zoo.addAuthInfo("digest",auth.getBytes());
            zoo.create(path, "znode".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            registerCode=false;
            e.printStackTrace();
        } catch (InterruptedException e) {
            registerCode=false;
            e.printStackTrace();
        }

        return registerCode;
    }


    public Stat getZNodeStatsReg(String name) throws KeeperException,
                InterruptedException {
            String path = registry + name;
            stat = zoo.exists(path, true);
            return stat;


    }


    public void SetWatchers(){
        try {
            zoo.getChildren("/System/Request/Enroll", this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            zoo.getChildren("/System/Request/Quit", this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}

