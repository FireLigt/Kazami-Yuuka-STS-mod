package YuukaMod.cards;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.abstracts.DynamicVariable;
import YuukaMod.Yuukamod;
import YuukaMod.util.CardStats;
import YuukaMod.util.TriFunction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static YuukaMod.util.GeneralUtils.removePrefix;
import static YuukaMod.util.TextureLoader.getCardTextureString;


public abstract class BaseCard extends CustomCard {
    final private static Map<String, DynamicVariable> customVars = new HashMap<>();

    protected static String makeID(String name) { return Yuukamod.makeID(name); }
    protected CardStrings cardStrings;

    protected boolean upgradesDescription;

    protected int baseCost;

    protected boolean upgradeCost;
    protected int costUpgrade;

    protected boolean upgradeDamage;
    protected boolean upgradeBlock;
    protected boolean upgradeMagic;
    protected boolean upgradeSecondMagic;

    protected int damageUpgrade;
    protected int blockUpgrade;
    protected int magicUpgrade;
    protected int secondMagicUpgrade;

    public int baseSecondMagicNumber;
    public int secondMagicNumber;

    private static DynamicVariable secondMagicVar;

    static {
        secondMagicVar = new DynamicVariable() {
            @Override
            public String key() {
                return makeID("SecondMagic");
            }

            @Override
            public boolean isModified(AbstractCard c) {
                if (c instanceof BaseCard) {
                    BaseCard bc = (BaseCard) c;
                    return bc.baseSecondMagicNumber != bc.secondMagicNumber;
                }
                return false;
            }

            @Override
            public int value(AbstractCard c) {
                if (c instanceof BaseCard) {
                    return ((BaseCard) c).secondMagicNumber;
                }
                return 0;
            }

            @Override
            public int baseValue(AbstractCard c) {
                if (c instanceof BaseCard) {
                    return ((BaseCard) c).baseSecondMagicNumber;
                }
                return 0;
            }

            @Override
            public boolean upgraded(AbstractCard c) {
                if (c instanceof BaseCard) {
                    return ((BaseCard) c).upgradeSecondMagic;
                }
                return false;
            }
        };
        BaseMod.addDynamicVariable(secondMagicVar);
    }

    protected boolean baseExhaust = false;
    protected boolean upgExhaust = false;
    protected boolean baseEthereal = false;
    protected boolean upgEthereal = false;
    protected boolean baseInnate = false;
    protected boolean upgInnate = false;
    protected boolean baseRetain = false;
    protected boolean upgRetain = false;

    final protected Map<String, LocalVarInfo> cardVariables = new HashMap<>();

    public Color rightGlowColor;

    public BaseCard(String ID, CardStats info) {
        this(ID, info, getCardTextureString(removePrefix(ID), info.cardType));
    }
    public BaseCard(String ID, CardStats info, String cardImage) {
        this(ID, info.baseCost, info.cardType, info.cardTarget, info.cardRarity, info.cardColor, cardImage);
    }
    public BaseCard(String ID, int cost, CardType cardType, CardTarget target, CardRarity rarity, CardColor color) {
        this(ID, cost, cardType, target, rarity, color, getCardTextureString(removePrefix(ID), cardType));
    }
    public BaseCard(String ID, int cost, CardType cardType, CardTarget target, CardRarity rarity, CardColor color, String cardImage)
    {
        super(ID, getName(ID), cardImage, cost, getInitialDescription(ID), cardType, color, rarity, target);
        this.cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);
        this.originalName = cardStrings.NAME;

        this.baseCost = cost;

        this.upgradesDescription = cardStrings.UPGRADE_DESCRIPTION != null;
        this.upgradeCost = false;
        this.upgradeDamage = false;
        this.upgradeBlock = false;
        this.upgradeMagic = false;
        this.upgradeSecondMagic = false;

        this.costUpgrade = cost;
        this.damageUpgrade = 0;
        this.blockUpgrade = 0;
        this.magicUpgrade = 0;
        this.secondMagicUpgrade = 0;
    }

    private static String getName(String ID) {
        return CardCrawlGame.languagePack.getCardStrings(ID).NAME;
    }
    private static String getInitialDescription(String ID) {
        return CardCrawlGame.languagePack.getCardStrings(ID).DESCRIPTION;
    }

    //Methods meant for constructor use
    protected final void setDamage(int damage)
    {
        this.setDamage(damage, 0);
    }
    protected final void setDamage(int damage, int damageUpgrade)
    {
        this.baseDamage = this.damage = damage;
        if (damageUpgrade != 0)
        {
            this.upgradeDamage = true;
            this.damageUpgrade = damageUpgrade;
        }
    }

    protected final void setBlock(int block)
    {
        this.setBlock(block, 0);
    }
    protected final void setBlock(int block, int blockUpgrade)
    {
        this.baseBlock = this.block = block;
        if (blockUpgrade != 0)
        {
            this.upgradeBlock = true;
            this.blockUpgrade = blockUpgrade;
        }
    }

    protected final void setMagic(int magic)
    {
        this.setMagic(magic, 0);
    }
    protected final void setMagic(int magic, int magicUpgrade)
    {
        this.baseMagicNumber = this.magicNumber = magic;
        if (magicUpgrade != 0)
        {
            this.upgradeMagic = true;
            this.magicUpgrade = magicUpgrade;
        }
    }

    protected final void setSecondMagic(int magic)
    {
        this.setSecondMagic(magic, 0);
    }
    protected final void setSecondMagic(int magic, int magicUpgrade)
    {
        this.baseSecondMagicNumber = this.secondMagicNumber = magic;
        if (magicUpgrade != 0)
        {
            this.upgradeSecondMagic = true;
            this.secondMagicUpgrade = magicUpgrade;
        }
    }

    protected final void setCustomVar(String key, int base) {
        this.setCustomVar(key, base, 0);
    }
    protected final void setCustomVar(String key, int base, int upgrade) {
        setCustomVarValue(key, base, upgrade);

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }

    protected enum VariableType {
        DAMAGE,
        BLOCK,
        MAGIC
    }
    protected final void setCustomVar(String key, VariableType type, int base) {
        setCustomVar(key, type, base, 0);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade) {
        setCustomVarValue(key, base, upgrade);

        switch (type) {
            case DAMAGE:
                calculateVarAsDamage(key);
                break;
            case BLOCK:
                calculateVarAsBlock(key);
                break;
        }

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }
    protected final void setCustomVar(String key, VariableType type, int base, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc) {
        setCustomVar(key, type, base, 0, preCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc) {
        setCustomVar(key, type, base, upgrade, preCalc, LocalVarInfo::noCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc, TriFunction<BaseCard, AbstractMonster, Integer, Integer> postCalc) {
        setCustomVar(key, type, base, 0, preCalc, postCalc);
    }
    protected final void setCustomVar(String key, VariableType type, int base, int upgrade, TriFunction<BaseCard, AbstractMonster, Integer, Integer> preCalc, TriFunction<BaseCard, AbstractMonster, Integer, Integer> postCalc) {
        setCustomVarValue(key, base, upgrade);

        switch (type) {
            case DAMAGE:
                setVarCalculation(key, (c, m, baseVal)->{
                    boolean wasMultiDamage = c.isMultiDamage;
                    c.isMultiDamage = false;

                    int origBase = c.baseDamage, origVal = c.damage;

                    c.baseDamage = preCalc.apply(c, m, baseVal);

                    if (m != null)
                        c.calculateCardDamage(m);
                    else
                        c.applyPowers();

                    c.damage = postCalc.apply(c, m, c.damage);

                    c.baseDamage = origBase;
                    c.isMultiDamage = wasMultiDamage;

                    int result = c.damage;
                    c.damage = origVal;

                    return result;
                });
                break;
            case BLOCK:
                setVarCalculation(key, (c, m, baseVal)->{
                    int origBase = c.baseBlock, origVal = c.block;

                    c.baseBlock = preCalc.apply(c, m, baseVal);

                    if (m != null)
                        c.calculateCardDamage(m);
                    else
                        c.applyPowers();

                    c.block = postCalc.apply(c, m, c.block);

                    c.baseBlock = origBase;
                    int result = c.block;
                    c.block = origVal;
                    return result;
                });
                break;
            default:
                setVarCalculation(key, (c, m, baseVal)->{
                    int tmp = baseVal;

                    tmp = preCalc.apply(c, m, tmp);
                    tmp = postCalc.apply(c, m, tmp);

                    return tmp;
                });
                break;
        }

        if (!customVars.containsKey(key)) {
            QuickDynamicVariable var = new QuickDynamicVariable(key);
            customVars.put(key, var);
            BaseMod.addDynamicVariable(var);
            initializeDescription();
        }
    }

    private void setCustomVarValue(String key, int base, int upg) {
        cardVariables.compute(key, (k, old)->{
            if (old == null) {
                return new LocalVarInfo(base, upg);
            }
            else {
                old.base = base;
                old.upgrade = upg;
                return old;
            }
        });
    }

    protected final void colorCustomVar(String key, Color normalColor) {
        colorCustomVar(key, normalColor, Settings.GREEN_TEXT_COLOR, Settings.RED_TEXT_COLOR, Settings.GREEN_TEXT_COLOR);
    }
    protected final void colorCustomVar(String key, Color normalColor, Color increasedColor, Color decreasedColor) {
        colorCustomVar(key, normalColor, increasedColor, decreasedColor, increasedColor);
    }
    protected final void colorCustomVar(String key, Color normalColor, Color increasedColor, Color decreasedColor, Color upgradedColor) {
        LocalVarInfo var = getCustomVar(key);
        if (var == null) {
            throw new IllegalArgumentException("Attempted to set color of variable that hasn't been registered.");
        }

        var.normalColor = normalColor;
        var.increasedColor = increasedColor;
        var.decreasedColor = decreasedColor;
        var.upgradedColor = upgradedColor;
    }


    private LocalVarInfo getCustomVar(String key) {
        return cardVariables.get(key);
    }

    protected void calculateVarAsDamage(String key) {
        setVarCalculation(key, (c, m, base)->{
            boolean wasMultiDamage = c.isMultiDamage;
            c.isMultiDamage = false;

            int origBase = c.baseDamage, origVal = c.damage;

            c.baseDamage = base;
            if (m != null)
                c.calculateCardDamage(m);
            else
                c.applyPowers();

            c.baseDamage = origBase;
            c.isMultiDamage = wasMultiDamage;

            int result = c.damage;
            c.damage = origVal;

            return result;
        });
    }
    protected void calculateVarAsBlock(String key) {
        setVarCalculation(key, (c, m, base)->{
            int origBase = c.baseBlock, origVal = c.block;

            c.baseBlock = base;
            if (m != null)
                c.calculateCardDamage(m);
            else
                c.applyPowers();

            c.baseBlock = origBase;
            int result = c.block;
            c.block = origVal;
            return result;
        });
    }
    protected void setVarCalculation(String key, TriFunction<BaseCard, AbstractMonster, Integer, Integer> calculation) {
        cardVariables.get(key).calculation = calculation;
    }

    public int customVarBase(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return -1;
        return var.base;
    }
    public int customVar(String key) {
        LocalVarInfo var = cardVariables == null ? null : cardVariables.get(key); //Prevents crashing when used with dynamic text
        if (var == null)
            return -1;
        return var.value;
    }
    public int[] customVarMulti(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return null;
        return var.aoeValue;
    }
    public boolean isCustomVarModified(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return false;
        return var.isModified();
    }
    public boolean customVarUpgraded(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null)
            return false;
        return var.upgraded;
    }


    protected final void setCostUpgrade(int costUpgrade)
    {
        this.costUpgrade = costUpgrade;
        this.upgradeCost = true;
    }
    protected final void setExhaust(boolean exhaust) { this.setExhaust(exhaust, exhaust); }
    protected final void setEthereal(boolean ethereal) { this.setEthereal(ethereal, ethereal); }
    protected final void setInnate(boolean innate) {this.setInnate(innate, innate); }
    protected final void setSelfRetain(boolean retain) {this.setSelfRetain(retain, retain); }
    protected final void setExhaust(boolean baseExhaust, boolean upgExhaust)
    {
        this.baseExhaust = baseExhaust;
        this.upgExhaust = upgExhaust;
        this.exhaust = baseExhaust;
    }
    protected final void setEthereal(boolean baseEthereal, boolean upgEthereal)
    {
        this.baseEthereal = baseEthereal;
        this.upgEthereal = upgEthereal;
        this.isEthereal = baseEthereal;
    }
    protected void setInnate(boolean baseInnate, boolean upgInnate)
    {
        this.baseInnate = baseInnate;
        this.upgInnate = upgInnate;
        this.isInnate = baseInnate;
    }
    protected void setSelfRetain(boolean baseRetain, boolean upgRetain)
    {
        this.baseRetain = baseRetain;
        this.upgRetain = upgRetain;
        this.selfRetain = baseRetain;
    }


    @Override
    public AbstractCard makeStatEquivalentCopy() {
        AbstractCard candidate = super.makeStatEquivalentCopy();

        if (candidate instanceof BaseCard) {
            BaseCard card = (BaseCard) candidate;
            card.rawDescription = this.rawDescription;
            card.upgradesDescription = this.upgradesDescription;

            card.baseCost = this.baseCost;

            card.upgradeCost = this.upgradeCost;
            card.upgradeDamage = this.upgradeDamage;
            card.upgradeBlock = this.upgradeBlock;
            card.upgradeMagic = this.upgradeMagic;
            card.upgradeSecondMagic = this.upgradeSecondMagic;

            card.costUpgrade = this.costUpgrade;
            card.damageUpgrade = this.damageUpgrade;
            card.blockUpgrade = this.blockUpgrade;
            card.magicUpgrade = this.magicUpgrade;
            card.secondMagicUpgrade = this.secondMagicUpgrade;

            card.baseSecondMagicNumber = this.baseSecondMagicNumber;
            card.secondMagicNumber = this.secondMagicNumber;

            card.baseExhaust = this.baseExhaust;
            card.upgExhaust = this.upgExhaust;
            card.baseEthereal = this.baseEthereal;
            card.upgEthereal = this.upgEthereal;
            card.baseInnate = this.baseInnate;
            card.upgInnate = this.upgInnate;
            card.baseRetain = this.baseRetain;
            card.upgRetain = this.upgRetain;

            for (Map.Entry<String, LocalVarInfo> varEntry : cardVariables.entrySet()) {
                LocalVarInfo target = card.getCustomVar(varEntry.getKey()),
                        current = varEntry.getValue();
                if (target == null) {
                    card.setCustomVar(varEntry.getKey(), current.base, current.upgrade);
                    target = card.getCustomVar(varEntry.getKey());
                }
                target.base = current.base;
                target.value = current.value;
                target.aoeValue = current.aoeValue;
                target.upgrade = current.upgrade;
                target.calculation = current.calculation;
            }
        }

        return candidate;
    }

    @Override
    public void upgrade()
    {
        if (!upgraded)
        {
            this.upgradeName();

            if (this.upgradesDescription)
            {
                if (cardStrings.UPGRADE_DESCRIPTION == null)
                {
                    Yuukamod.logger.error("Card " + cardID + " upgrades description and has null upgrade description.");
                }
                else
                {
                    this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
                }
            }

            if (upgradeCost)
            {
                if (isCostModified && this.cost < this.baseCost && this.cost >= 0) {
                    int diff = this.costUpgrade - this.baseCost; //how the upgrade alters cost
                    this.upgradeBaseCost(this.cost + diff);
                    if (this.cost < 0)
                        this.cost = 0;
                }
                else {
                    upgradeBaseCost(costUpgrade);
                }
            }

            if (upgradeDamage)
                this.upgradeDamage(damageUpgrade);

            if (upgradeBlock)
                this.upgradeBlock(blockUpgrade);

            if (upgradeMagic)
                this.upgradeMagicNumber(magicUpgrade);

            if (upgradeSecondMagic) {
                this.baseSecondMagicNumber += secondMagicUpgrade;
                this.secondMagicNumber = this.baseSecondMagicNumber;
            }

            for (LocalVarInfo var : cardVariables.values()) {
                upgradeCustomVar(var);
            }

            if (baseExhaust ^ upgExhaust)
                this.exhaust = upgExhaust;

            if (baseInnate ^ upgInnate)
                this.isInnate = upgInnate;

            if (baseEthereal ^ upgEthereal)
                this.isEthereal = upgEthereal;

            if (baseRetain ^ upgRetain)
                this.selfRetain = upgRetain;


            this.initializeDescription();
        }
    }

    protected void upgradeCustomVar(String key) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null) {
            throw new NullPointerException("Custom variable with key " + key + " does not exist in " + getClass().getName());
        }
        upgradeCustomVar(var, var.upgrade);
    }

    protected void upgradeCustomVar(String key, int amount) {
        LocalVarInfo var = cardVariables.get(key);
        if (var == null) {
            throw new NullPointerException("Custom variable with key " + key + " does not exist in " + getClass().getName());
        }
        upgradeCustomVar(var, amount);
    }

    protected void upgradeCustomVar(LocalVarInfo var) {
        upgradeCustomVar(var, var.upgrade);
    }

    protected void upgradeCustomVar(LocalVarInfo var, int amt) {
        if (amt != 0) {
            var.base += amt;
            var.value = var.base;
            var.upgraded = true;
        }
    }

    boolean inCalc = false;
    @Override
    public void applyPowers() {
        if (!inCalc) {
            inCalc = true;
            for (LocalVarInfo var : cardVariables.values()) {
                var.value = var.calculation.apply(this, null, var.base);
            }
            if (isMultiDamage) {
                ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
                AbstractMonster m;
                for (LocalVarInfo var : cardVariables.values()) {
                    if (var.aoeValue == null || var.aoeValue.length != monsters.size())
                        var.aoeValue = new int[monsters.size()];

                    for (int i = 0; i < monsters.size(); ++i) {
                        m = monsters.get(i);
                        var.aoeValue[i] = var.calculation.apply(this, m, var.base);
                    }
                }
            }
            inCalc = false;
        }

        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        if (!inCalc) {
            inCalc = true;
            for (LocalVarInfo var : cardVariables.values()) {
                var.value = var.calculation.apply(this, m, var.base);
            }
            if (isMultiDamage) {
                ArrayList<AbstractMonster> monsters = AbstractDungeon.getCurrRoom().monsters.monsters;
                for (LocalVarInfo var : cardVariables.values()) {
                    if (var.aoeValue == null || var.aoeValue.length != monsters.size())
                        var.aoeValue = new int[monsters.size()];

                    for (int i = 0; i < monsters.size(); ++i) {
                        m = monsters.get(i);
                        var.aoeValue[i] = var.calculation.apply(this, m, var.base);
                    }
                }
            }
            inCalc = false;
        }

        super.calculateCardDamage(m);
    }

    @Override
    public void resetAttributes() {
        super.resetAttributes();

        for (LocalVarInfo var : cardVariables.values()) {
            var.value = var.base;
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        if (!super.canUse(p, m)) return false;
        return true;
    }

    @Override
    public void triggerOnGlowCheck() {
        this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (rightGlowColor != null) {
            renderRightGlow(sb);
        }
    }

    private void renderRightGlow(SpriteBatch sb) {
        try {
            float t = 0.75F * (1.0F + MathUtils.cos(System.currentTimeMillis() * 0.002F)) + 0.25F;

            float halfW = 125.0F * this.drawScale;
            float halfH = 95.0F * this.drawScale;
            float centerX = this.current_x;
            float centerY = this.current_y;
            float bottomY = centerY - halfH;

            sb.flush();

            // Convert world coordinates to screen coordinates for scissor
            Matrix4 combined = new Matrix4(sb.getProjectionMatrix());
            combined.mul(sb.getTransformMatrix());

            Vector3 bl = new Vector3(centerX, bottomY, 0);
            Vector3 tr = new Vector3(centerX + halfW, bottomY + halfH * 2.0F, 0);
            bl.prj(combined);
            tr.prj(combined);

            float sx = Gdx.graphics.getWidth();
            float sy = Gdx.graphics.getHeight();
            int scissorX = (int)((bl.x + 1.0F) * 0.5F * sx);
            int scissorY = (int)((bl.y + 1.0F) * 0.5F * sy);
            int scissorW = (int)((tr.x + 1.0F) * 0.5F * sx) - scissorX;
            int scissorH = (int)((tr.y + 1.0F) * 0.5F * sy) - scissorY;

            if (scissorW > 0 && scissorH > 0) {
                Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
                Gdx.gl.glScissor(scissorX, scissorY, scissorW, scissorH);

                sb.setColor(new Color(this.rightGlowColor.r, this.rightGlowColor.g, this.rightGlowColor.b, t * this.rightGlowColor.a));
                sb.setBlendFunction(770, 1);
                sb.draw(ImageMaster.WHITE_SQUARE_IMG,
                    centerX - 125.0F, centerY - 95.0F,
                    125.0F, 95.0F,
                    250.0F, 190.0F,
                    this.drawScale, this.drawScale,
                    this.angle,
                    0, 0, 1, 1,
                    false, false);
                sb.setBlendFunction(770, 771);

                sb.flush();
                Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
            }

            sb.setColor(Color.WHITE);
        } catch (Exception e) {
            Yuukamod.logger.error("RightGlow error: ", e);
        }
    }

    private static class QuickDynamicVariable extends DynamicVariable {
        final String localKey, key;

        private BaseCard current = null;

        public QuickDynamicVariable(String key) {
            this.localKey = key;
            this.key = makeID(key);
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public void setIsModified(AbstractCard c, boolean v) {
            if (c instanceof BaseCard) {
                LocalVarInfo var = ((BaseCard) c).getCustomVar(localKey);
                if (var != null)
                    var.forceModified = v;
            }
        }

        @Override
        public boolean isModified(AbstractCard c) {
            return c instanceof BaseCard && (current = (BaseCard) c).isCustomVarModified(localKey);
        }

        @Override
        public int value(AbstractCard c) {
            return c instanceof BaseCard ? ((BaseCard) c).customVar(localKey) : 0;
        }

        @Override
        public int baseValue(AbstractCard c) {
            return c instanceof BaseCard ? ((BaseCard) c).customVarBase(localKey) : 0;
        }

        @Override
        public boolean upgraded(AbstractCard c) {
            return c instanceof BaseCard && ((BaseCard) c).customVarUpgraded(localKey);
        }

        public Color getNormalColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.CREAM_COLOR;

            return var.normalColor;
        }

        public Color getUpgradedColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.GREEN_TEXT_COLOR;

            return var.upgradedColor;
        }

        public Color getIncreasedValueColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.GREEN_TEXT_COLOR;

            return var.increasedColor;
        }

        public Color getDecreasedValueColor() {
            LocalVarInfo var;
            if (current == null || (var = current.getCustomVar(localKey)) == null)
                return Settings.RED_TEXT_COLOR;

            return var.decreasedColor;
        }
    }

    protected static class LocalVarInfo {
        int base, value, upgrade;
        int[] aoeValue = null;
        boolean upgraded = false;
        boolean forceModified = false;
        Color normalColor = Settings.CREAM_COLOR;
        Color upgradedColor = Settings.GREEN_TEXT_COLOR;
        Color increasedColor = Settings.GREEN_TEXT_COLOR;
        Color decreasedColor = Settings.RED_TEXT_COLOR;

        TriFunction<BaseCard, AbstractMonster, Integer, Integer> calculation = LocalVarInfo::noCalc;

        public LocalVarInfo(int base, int upgrade) {
            this.base = this.value = base;
            this.upgrade = upgrade;
        }

        private static int noCalc(BaseCard c, AbstractMonster m, int base) {
            return base;
        }

        public boolean isModified() {
            return forceModified || base != value;
        }
    }
    public static com.badlogic.gdx.graphics.Color goldGlowColor() {
        return GOLD_BORDER_GLOW_COLOR.cpy();
    }

    public static com.badlogic.gdx.graphics.Color redGlowColor() {
        return com.badlogic.gdx.graphics.Color.RED.cpy();
    }

    public static class CustomTags
    {
        @SpireEnum(name = "YUUKA_FLOWER") public static AbstractCard.CardTags FLOWER;
        @SpireEnum(name = "YUUKA_MAGIC_CANNON") public static AbstractCard.CardTags MAGICCANNON;
        @SpireEnum(name = "YUUKA_PHYSICAL_ARTS") public static AbstractCard.CardTags PHYSICAL_ARTS;
        @SpireEnum(name = "YUUKA_DANMAKU") public static AbstractCard.CardTags DANMAKU;
        @SpireEnum(name = "YUUKA_PLANT") public static AbstractCard.CardTags PLANT;
        @SpireEnum(name = "YUUKA_UNIQUE") public static AbstractCard.CardTags UNIQUE;
    }
    }
