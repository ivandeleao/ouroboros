package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.PisBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class PisBsDAO {

    public List<PisBs> findAll() {
        List<PisBs> pisBsList = null;
        try {
            Query query = emBs.createQuery("from PisBs i");

            pisBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return pisBsList;
    }
}
