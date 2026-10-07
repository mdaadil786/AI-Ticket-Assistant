import { describe, expect, it } from 'vitest'
import { assistantStreamUrl } from '../api/client'

describe('assistantStreamUrl', () => {
  it('builds encoded stream url', () => {
    expect(assistantStreamUrl('My payment succeeded but the order was not created. What should I do?')).toContain('My%20payment')
  })
})
