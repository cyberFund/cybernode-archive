package fund.cyber.node.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "block")
public class Block extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(name = "version")
  private long version;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "prev_block_id")
  private Block prevBlock;

  @Column(name = "merkle_root")
  private String merkleRoot;

  @Column(name = "time")
  private long time;

  @Column(name = "difficulty")
  private long difficulty;

  @Column(name = "nonce")
  private long nonce;

  @OneToMany(mappedBy = "block", fetch = LAZY)
  private List<Transaction> transactions;

  @Column(name = "hash")
  private String hash;

  @Column(name = "height")
  private int height;

  @Column(name = "chainwork")
  private BigInteger chainwork;

  @Column(name = "chainhead")
  private boolean chainhead;

  @Column(name = "indexed")
  private boolean indexed;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public Block getPrevBlock() {
    return prevBlock;
  }

  public void setPrevBlock(Block prevBlock) {
    this.prevBlock = prevBlock;
  }

  public String getMerkleRoot() {
    return merkleRoot;
  }

  public void setMerkleRoot(String merkleRoot) {
    this.merkleRoot = merkleRoot;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(long difficulty) {
    this.difficulty = difficulty;
  }

  public long getNonce() {
    return nonce;
  }

  public void setNonce(long nonce) {
    this.nonce = nonce;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public BigInteger getChainwork() {
    return chainwork;
  }

  public void setChainwork(BigInteger chainwork) {
    this.chainwork = chainwork;
  }

  public boolean isChainhead() {
    return chainhead;
  }

  public void setChainhead(boolean chainhead) {
    this.chainhead = chainhead;
  }

  public boolean isIndexed() {
    return indexed;
  }

  public void setIndexed(boolean full) {
    this.indexed = full;
  }

  @Override
  public String toString() {
    return "Block [username=" + hash + "]";
  }

}
