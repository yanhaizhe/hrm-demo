package org.dayatang.hrm.organisation.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.dayatang.domain.AbstractEntity;
import org.dayatang.utils.Assert;
import org.dayatang.utils.DateUtils;

@Entity
@Table(name = "accountabilities")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CATEGORY", discriminatorType = DiscriminatorType.STRING)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Accountability.findAccountabilitiesByParty",
            query = "select o from Accountability o where (o.commissioner = :party or o.responsible = :party) and o.fromDate <= :date and o.toDate > :date")})
public abstract class Accountability<C extends Party, R extends Party> extends AbstractEntity {

    private static final long serialVersionUID = 3456398163374995470L;

    private C commissioner;

    private R responsible;

    private Date fromDate;

    private Date toDate;

    protected Accountability() {
    }

    public Accountability(C commissioner, R responsible, Date fromDate) {
        this.commissioner = commissioner;
        this.responsible = responsible;
        this.fromDate = new Date(fromDate.getTime());
        this.toDate = DateUtils.MAX_DATE;
    }

    @ManyToOne(targetEntity = Party.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "commissioner_id")
    public C getCommissioner() {
        return commissioner;
    }

    void setCommissioner(C commissioner) {
        this.commissioner = commissioner;
    }

    @ManyToOne(targetEntity = Party.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "responsible_id")
    public R getResponsible() {
        return responsible;
    }

    void setResponsible(R responsible) {
        this.responsible = responsible;
    }

    @Temporal(TemporalType.DATE)
    public Date getFromDate() {
        return new Date(fromDate.getTime());
    }

    void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "to_date")
    public Date getToDate() {
        return new Date(toDate.getTime());
    }

    void setToDate(Date toDate) {
        this.toDate = new Date(toDate.getTime());
    }

    @Override
    public String[] businessKeys() {
        return new String[]{"commissioner", "responsible", "fromDate", "toDate"};
    }

    public void terminate(Date date) {
        Assert.notNull(date, "Terminate Date is null!");
        this.toDate = new Date(date.getTime());
        save();
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Accountability> List<T> findAccountabilities(Class<T> accountabilityClass, Date date) {
        return getRepository().createCriteriaQuery(accountabilityClass).le("fromDate", date).gt("toDate", date).list();
    }

    @SuppressWarnings("rawtypes")
    public static List<Accountability> findAccountabilitiesByParty(Party party, Date date) {
        return getRepository().createNamedQuery("Accountability.findAccountabilitiesByParty")
                .addParameter("party", party).addParameter("date", date).list();
    }

}
