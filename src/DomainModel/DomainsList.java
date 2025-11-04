package DomainModel;

import java.util.ArrayList;
import java.util.Iterator;

public class DomainsList implements Iterable<Domain> {
    private ArrayList<Domain> domains = new ArrayList<>();
    public DomainsList() {
        domains = new ArrayList<Domain>();
    }
    public void add(Domain domain) {
        domains.add(domain);
    }
    public ArrayList<Domain> getDomains() {
        return domains;
    }
    public Domain get(int index) {
        return domains.get(index);
    }
    public int size() {
        return domains.size();
    }


    @Override
    public Iterator<Domain> iterator() {
        return domains.iterator();
    }

    public void remove(int i) {
        domains.remove(i);
    }
}
