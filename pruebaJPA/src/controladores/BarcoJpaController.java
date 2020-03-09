/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Barco;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Socio;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author labcisco
 */
public class BarcoJpaController implements Serializable {

    public BarcoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Barco barco) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Socio idSocio = barco.getIdSocio();
            if (idSocio != null) {
                idSocio = em.getReference(idSocio.getClass(), idSocio.getIdSocio());
                barco.setIdSocio(idSocio);
            }
            em.persist(barco);
            if (idSocio != null) {
                idSocio.getBarcoList().add(barco);
                idSocio = em.merge(idSocio);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBarco(barco.getIdBarco()) != null) {
                throw new PreexistingEntityException("Barco " + barco + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Barco barco) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Barco persistentBarco = em.find(Barco.class, barco.getIdBarco());
            Socio idSocioOld = persistentBarco.getIdSocio();
            Socio idSocioNew = barco.getIdSocio();
            if (idSocioNew != null) {
                idSocioNew = em.getReference(idSocioNew.getClass(), idSocioNew.getIdSocio());
                barco.setIdSocio(idSocioNew);
            }
            barco = em.merge(barco);
            if (idSocioOld != null && !idSocioOld.equals(idSocioNew)) {
                idSocioOld.getBarcoList().remove(barco);
                idSocioOld = em.merge(idSocioOld);
            }
            if (idSocioNew != null && !idSocioNew.equals(idSocioOld)) {
                idSocioNew.getBarcoList().add(barco);
                idSocioNew = em.merge(idSocioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = barco.getIdBarco();
                if (findBarco(id) == null) {
                    throw new NonexistentEntityException("The barco with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Barco barco;
            try {
                barco = em.getReference(Barco.class, id);
                barco.getIdBarco();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The barco with id " + id + " no longer exists.", enfe);
            }
            Socio idSocio = barco.getIdSocio();
            if (idSocio != null) {
                idSocio.getBarcoList().remove(barco);
                idSocio = em.merge(idSocio);
            }
            em.remove(barco);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Barco> findBarcoEntities() {
        return findBarcoEntities(true, -1, -1);
    }

    public List<Barco> findBarcoEntities(int maxResults, int firstResult) {
        return findBarcoEntities(false, maxResults, firstResult);
    }

    private List<Barco> findBarcoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Barco.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Barco findBarco(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Barco.class, id);
        } finally {
            em.close();
        }
    }

    public int getBarcoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Barco> rt = cq.from(Barco.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
