export const cleanJpaModel = (jpaModel) => {
  return jpaModel.replace(/\n/g, ' ').replace(/\r/g, '');
};