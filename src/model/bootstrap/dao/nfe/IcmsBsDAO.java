package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.IcmsBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class IcmsBsDAO {

    public List<IcmsBs> findAll() {
        List<IcmsBs> icmsBsList = null;
        try {
            Query query = emBs.createQuery("from IcmsBs i");

            icmsBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return icmsBsList;
    }
}
