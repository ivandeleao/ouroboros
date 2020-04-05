package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.IpiBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class IpiBsDAO {

    public List<IpiBs> findAll() {
        List<IpiBs> ipiBsList = null;
        try {
            Query query = emBs.createQuery("from IpiBs i");

            ipiBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return ipiBsList;
    }
}
